package com.web;

import weka.classifiers.CostMatrix;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class Output {

    protected Instances m_Instances;

    protected void startClassifier() throws Exception {

        StringBuffer outBuff = new StringBuffer();
        DataSource source = new DataSource("https://storm.cis.fordham.edu/~gweiss/data-mining/weka-data/weather.arff");
        Instances inst = source.getDataSet();

        // TODO: Add way to differentiate between datasets

        inst.attribute(classIndex).numValues();
        int numClasses = m_Instances.attribute(classIndex).numValues();

        costMatrix = new CostMatrix((CostMatrix) m_CostMatrixEditor.getValue());

        eval = new Evaluation(inst, costMatrix);
        Instances inst = new Instances(m_Instances);

        outBuff.append(eval.toSummaryString(outputEntropy) + "\n");

        outBuff.append(eval.toClassDetailsString() + "\n");

        outBuff.append(eval.toMatrixString() + "\n");











        if (m_RunThread == null) {
            synchronized (this) {
                m_StartBut.setEnabled(false);
                m_StopBut.setEnabled(true);
            }
            m_RunThread = new Thread() {
                @Override
                public void run() {
                    m_CEPanel.addToHistory();

                    // Copy the current state of things
                    m_Log.statusMessage("Setting up...");
                    CostMatrix costMatrix = null;
                    Instances inst = new Instances(m_Instances);
                    DataSource source = null;
                    Instances userTestStructure = null;
                    ClassifierErrorsPlotInstances plotInstances = null;

                    // for timing
                    long trainTimeStart = 0, trainTimeElapsed = 0;
                    long testTimeStart = 0, testTimeElapsed = 0;

                    try {
                        if (m_TestLoader != null && m_TestLoader.getStructure() != null) {
                            /*
                             * if (m_ClassifierEditor.getValue() instanceof BatchPredictor &&
                             * ((BatchPredictor) m_ClassifierEditor.getValue())
                             * .implementsMoreEfficientBatchPrediction() && m_TestLoader
                             * instanceof ArffLoader) { // we're not really streaming test
                             * instances in this case... ((ArffLoader)
                             * m_TestLoader).setRetainStringVals(true); }
                             */
                            if (m_TestLoader instanceof ArffLoader
                                    && m_StoreTestDataAndPredictionsBut.isSelected()) {
                                ((ArffLoader) m_TestLoader).setRetainStringVals(true);
                            }
                            m_TestLoader.reset();
                            source = new DataSource(m_TestLoader);
                            userTestStructure = source.getStructure();
                            userTestStructure.setClassIndex(m_TestClassIndex);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    if (m_EvalWRTCostsBut.isSelected()) {
                        costMatrix =
                                new CostMatrix((CostMatrix) m_CostMatrixEditor.getValue());
                    }
                    boolean outputModel = m_OutputModelBut.isSelected();
                    boolean outputModelsForTrainingSplits =
                            m_OutputModelsForTrainingSplitsBut.isSelected();
                    boolean outputConfusion = m_OutputConfusionBut.isSelected();
                    boolean outputPerClass = m_OutputPerClassBut.isSelected();
                    boolean outputSummary = true;
                    boolean outputEntropy = m_OutputEntropyBut.isSelected();
                    boolean saveVis = m_StoreTestDataAndPredictionsBut.isSelected();
                    boolean collectPredictionsForEvaluation = m_CollectPredictionsForEvaluationBut.isSelected();
                    boolean outputPredictionsText =
                            (m_ClassificationOutputEditor.getValue().getClass() != Null.class);

                    String grph = null;

                    int testMode = 0;
                    int numFolds = 10;
                    double percent = 66;
                    int classIndex = m_ClassCombo.getSelectedIndex();
                    inst.setClassIndex(classIndex);
                    Classifier classifier = (Classifier) m_ClassifierEditor.getValue();
                    if (classifier instanceof weka.core.LogHandler) {
                        ((weka.core.LogHandler) classifier).setLog(m_Log);
                    }
                    Classifier template = null;
                    try {
                        template = AbstractClassifier.makeCopy(classifier);
                    } catch (Exception ex) {
                        m_Log.logMessage("Problem copying classifier: " + ex.getMessage());
                    }
                    Classifier fullClassifier = null;
                    StringBuffer outBuff = new StringBuffer();
                    AbstractOutput classificationOutput = null;
                    if (outputPredictionsText) {
                        classificationOutput =
                                (AbstractOutput) m_ClassificationOutputEditor.getValue();
                        Instances header = new Instances(inst, 0);
                        header.setClassIndex(classIndex);
                        classificationOutput.setHeader(header);
                        classificationOutput.setBuffer(outBuff);
                    }
                    String name =
                            (new SimpleDateFormat("HH:mm:ss - ")).format(new Date());
                    String cname = "";
                    String cmd = "";
                    Evaluation eval = null;
                    try {
                        if (m_CVBut.isSelected()) {
                            testMode = 1;
                            numFolds = Integer.parseInt(m_CVText.getText());
                            if (numFolds <= 1) {
                                throw new Exception("Number of folds must be greater than 1");
                            }
                        } else if (m_PercentBut.isSelected()) {
                            testMode = 2;
                            percent = Double.parseDouble(m_PercentText.getText());
                            if ((percent <= 0) || (percent >= 100)) {
                                throw new Exception("Percentage must be between 0 and 100");
                            }
                        } else if (m_TrainBut.isSelected()) {
                            testMode = 3;
                        } else if (m_TestSplitBut.isSelected()) {
                            testMode = 4;
                            // Check the test instance compatibility
                            if (source == null) {
                                throw new Exception("No user test set has been specified");
                            }

                            if (!(classifier instanceof weka.classifiers.misc.InputMappedClassifier)) {
                                if (!inst.equalHeaders(userTestStructure)) {
                                    boolean wrapClassifier = false;
                                    if (!Utils
                                            .getDontShowDialog("weka.gui.explorer.ClassifierPanel.AutoWrapInInputMappedClassifier")) {
                                        JCheckBox dontShow =
                                                new JCheckBox("Do not show this message again");
                                        Object[] stuff = new Object[2];
                                        stuff[0] =
                                                "Train and test set are not compatible.\n"
                                                        + "Would you like to automatically wrap the classifier in\n"
                                                        + "an \"InputMappedClassifier\" before proceeding?.\n";
                                        stuff[1] = dontShow;

                                        int result =
                                                JOptionPane.showConfirmDialog(ClassifierPanel.this,
                                                        stuff, "ClassifierPanel", JOptionPane.YES_OPTION);

                                        if (result == JOptionPane.YES_OPTION) {
                                            wrapClassifier = true;
                                        }

                                        if (dontShow.isSelected()) {
                                            String response = (wrapClassifier) ? "yes" : "no";
                                            Utils
                                                    .setDontShowDialogResponse(
                                                            "weka.gui.explorer.ClassifierPanel.AutoWrapInInputMappedClassifier",
                                                            response);
                                        }

                                    } else {
                                        // What did the user say - do they want to autowrap or not?
                                        String response =
                                                Utils
                                                        .getDontShowDialogResponse("weka.gui.explorer.ClassifierPanel.AutoWrapInInputMappedClassifier");
                                        if (response != null && response.equalsIgnoreCase("yes")) {
                                            wrapClassifier = true;
                                        }
                                    }

                                    if (wrapClassifier) {
                                        weka.classifiers.misc.InputMappedClassifier temp =
                                                new weka.classifiers.misc.InputMappedClassifier();

                                        // pass on the known test structure so that we get the
                                        // correct mapping report from the toString() method
                                        // of InputMappedClassifier
                                        temp.setClassifier(classifier);
                                        temp.setTestStructure(userTestStructure);
                                        classifier = temp;
                                    } else {
                                        throw new Exception(
                                                "Train and test set are not compatible\n"
                                                        + inst.equalHeadersMsg(userTestStructure));
                                    }
                                }
                            }

                        } else {
                            throw new Exception("Unknown test mode");
                        }

                        cname = classifier.getClass().getName();
                        if (cname.startsWith("weka.classifiers.")) {
                            name += cname.substring("weka.classifiers.".length());
                        } else {
                            name += cname;
                        }
                        cmd = classifier.getClass().getName();
                        if (classifier instanceof OptionHandler) {
                            cmd +=
                                    " "
                                            + Utils
                                            .joinOptions(((OptionHandler) classifier).getOptions());
                        }

                        // set up the structure of the plottable instances for
                        // visualization
                        plotInstances = ExplorerDefaults.getClassifierErrorsPlotInstances();
                        plotInstances
                                .setInstances(testMode == 4 ? userTestStructure : inst);
                        plotInstances.setClassifier(classifier);
                        plotInstances.setClassIndex(inst.classIndex());
                        plotInstances.setSaveForVisualization(saveVis);
                        plotInstances
                                .setPointSizeProportionalToMargin(m_errorPlotPointSizeProportionalToMargin
                                        .isSelected());

                        // Output some header information
                        m_Log.logMessage("Started " + cname);
                        m_Log.logMessage("Command: " + cmd);
                        if (m_Log instanceof TaskLogger) {
                            ((TaskLogger) m_Log).taskStarted();
                        }
                        outBuff.append("=== Run information ===\n\n");
                        outBuff.append("Scheme:       " + cname);
                        if (classifier instanceof OptionHandler) {
                            String[] o = ((OptionHandler) classifier).getOptions();
                            outBuff.append(" " + Utils.joinOptions(o));
                        }
                        outBuff.append("\n");
                        outBuff.append("Relation:     " + inst.relationName() + '\n');
                        outBuff.append("Instances:    " + inst.numInstances() + '\n');
                        outBuff.append("Attributes:   " + inst.numAttributes() + '\n');
                        if (inst.numAttributes() < 100) {
                            for (int i = 0; i < inst.numAttributes(); i++) {
                                outBuff.append("              " + inst.attribute(i).name()
                                        + '\n');
                            }
                        } else {
                            outBuff.append("              [list of attributes omitted]\n");
                        }

                        outBuff.append("Test mode:    ");
                        switch (testMode) {
                            case 3: // Test on training
                                outBuff.append("evaluate on training data\n");
                                break;
                            case 1: // CV mode
                                outBuff.append("" + numFolds + "-fold cross-validation\n");
                                break;
                            case 2: // Percent split
                                outBuff.append("split " + percent + "% train, remainder test\n");
                                break;
                            case 4: // Test on user split
                                if (source.isIncremental()) {
                                    outBuff.append("user supplied test set: "
                                            + " size unknown (reading incrementally)\n");
                                } else {
                                    outBuff.append("user supplied test set: "
                                            + source.getDataSet().numInstances() + " instances\n");
                                }
                                break;
                        }
                        if (costMatrix != null) {
                            outBuff.append("Evaluation cost matrix:\n")
                                    .append(costMatrix.toString()).append("\n");
                        }
                        outBuff.append("\n");
                        m_History.addResult(name, outBuff);
                        m_History.setSingle(name);

                        // Build the model and output it.
                        if (outputModel || (testMode == 3) || (testMode == 4)) {
                            m_Log.statusMessage("Building model on training data...");

                            trainTimeStart = System.currentTimeMillis();
                            classifier.buildClassifier(inst);
                            trainTimeElapsed = System.currentTimeMillis() - trainTimeStart;
                        }

                        if (outputModel) {
                            outBuff
                                    .append("=== Classifier model (full training set) ===\n\n");
                            outBuff.append(classifier.toString() + "\n");
                            outBuff.append("\nTime taken to build model: "
                                    + Utils.doubleToString(trainTimeElapsed / 1000.0, 2)
                                    + " seconds\n\n");
                            m_History.updateResult(name);
                            if (classifier instanceof Drawable) {
                                grph = null;
                                try {
                                    grph = ((Drawable) classifier).graph();
                                } catch (Exception ex) {
                                }
                            }
                            // copy full model for output
                            SerializedObject so = new SerializedObject(classifier);
                            fullClassifier = (Classifier) so.getObject();
                        }

                        switch (testMode) {
                            case 3: // Test on training
                                m_Log.statusMessage("Evaluating on training data...");
                                eval = new Evaluation(inst, costMatrix);

                                // make adjustments if the classifier is an InputMappedClassifier
                                eval =
                                        setupEval(eval, classifier, inst, costMatrix, plotInstances,
                                                classificationOutput, false, collectPredictionsForEvaluation);
                                eval.setMetricsToDisplay(m_selectedEvalMetrics);

                                // plotInstances.setEvaluation(eval);
                                plotInstances.setUp();

                                if (outputPredictionsText) {
                                    printPredictionsHeader(outBuff, classificationOutput,
                                            "training set");
                                }

                                testTimeStart = System.currentTimeMillis();
                                if (classifier instanceof BatchPredictor
                                        && ((BatchPredictor) classifier)
                                        .implementsMoreEfficientBatchPrediction()) {
                                    Instances toPred = new Instances(inst);
                                    for (int i = 0; i < toPred.numInstances(); i++) {
                                        toPred.instance(i).setClassMissing();
                                    }
                                    double[][] predictions =
                                            ((BatchPredictor) classifier)
                                                    .distributionsForInstances(toPred);
                                    plotInstances.process(inst, predictions, eval);
                                    if (outputPredictionsText) {
                                        for (int jj = 0; jj < inst.numInstances(); jj++) {
                                            classificationOutput.printClassification(predictions[jj],
                                                    inst.instance(jj), jj);
                                        }
                                    }
                                } else {
                                    for (int jj = 0; jj < inst.numInstances(); jj++) {
                                        plotInstances.process(inst.instance(jj), classifier, eval);

                                        if (outputPredictionsText) {
                                            classificationOutput.printClassification(classifier,
                                                    inst.instance(jj), jj);
                                        }
                                        if ((jj % 100) == 0) {
                                            m_Log
                                                    .statusMessage("Evaluating on training data. Processed "
                                                            + jj + " instances...");
                                        }
                                    }
                                }
                                testTimeElapsed = System.currentTimeMillis() - testTimeStart;
                                if (outputPredictionsText) {
                                    classificationOutput.printFooter();
                                }
                                if (outputPredictionsText
                                        && classificationOutput.generatesOutput()) {
                                    outBuff.append("\n");
                                }
                                outBuff.append("=== Evaluation on training set ===\n");
                                break;

                            case 1: // CV mode
                                m_Log.statusMessage("Randomizing instances...");
                                int rnd = 1;
                                try {
                                    rnd = Integer.parseInt(m_RandomSeedText.getText().trim());
                                    // System.err.println("Using random seed "+rnd);
                                } catch (Exception ex) {
                                    m_Log.logMessage("Trouble parsing random seed value");
                                    rnd = 1;
                                }
                                Random random = new Random(rnd);
                                inst.randomize(random);
                                if (inst.attribute(classIndex).isNominal()) {
                                    m_Log.statusMessage("Stratifying instances...");
                                    inst.stratify(numFolds);
                                }
                                eval = new Evaluation(inst, costMatrix);

                                // make adjustments if the classifier is an InputMappedClassifier
                                eval =
                                        setupEval(eval, classifier, inst, costMatrix, plotInstances,
                                                classificationOutput, false, collectPredictionsForEvaluation);
                                eval.setMetricsToDisplay(m_selectedEvalMetrics);

                                // plotInstances.setEvaluation(eval);
                                plotInstances.setUp();

                                if (outputPredictionsText) {
                                    printPredictionsHeader(outBuff, classificationOutput,
                                            "test data");
                                }

                                // Make some splits and do a CV
                                for (int fold = 0; fold < numFolds; fold++) {
                                    m_Log.statusMessage("Creating splits for fold " + (fold + 1)
                                            + "...");
                                    Instances train = inst.trainCV(numFolds, fold, random);

                                    // make adjustments if the classifier is an
                                    // InputMappedClassifier
                                    eval =
                                            setupEval(eval, classifier, train, costMatrix, plotInstances,
                                                    classificationOutput, true, collectPredictionsForEvaluation);
                                    eval.setMetricsToDisplay(m_selectedEvalMetrics);

                                    // eval.setPriors(train);
                                    m_Log.statusMessage("Building model for fold " + (fold + 1)
                                            + "...");
                                    Classifier current = null;
                                    try {
                                        current = AbstractClassifier.makeCopy(template);
                                        if (current instanceof weka.core.LogHandler) {
                                            ((weka.core.LogHandler) current).setLog(m_Log);
                                        }
                                    } catch (Exception ex) {
                                        m_Log.logMessage("Problem copying classifier: "
                                                + ex.getMessage());
                                    }
                                    current.buildClassifier(train);
                                    if (outputModelsForTrainingSplits) {
                                        outBuff.append("\n=== Classifier model for fold "
                                                + (fold + 1) + " ===\n\n");
                                        outBuff.append(current.toString() + "\n");
                                    }
                                    Instances test = inst.testCV(numFolds, fold);
                                    m_Log.statusMessage("Evaluating model for fold " + (fold + 1)
                                            + "...");

                                    if (classifier instanceof BatchPredictor
                                            && ((BatchPredictor) classifier)
                                            .implementsMoreEfficientBatchPrediction()) {
                                        Instances toPred = new Instances(test);
                                        for (int i = 0; i < toPred.numInstances(); i++) {
                                            toPred.instance(i).setClassMissing();
                                        }
                                        double[][] predictions =
                                                ((BatchPredictor) current)
                                                        .distributionsForInstances(toPred);
                                        plotInstances.process(test, predictions, eval);
                                        if (outputPredictionsText) {
                                            for (int jj = 0; jj < test.numInstances(); jj++) {
                                                classificationOutput.printClassification(predictions[jj],
                                                        test.instance(jj), jj);
                                            }
                                        }
                                    } else {
                                        for (int jj = 0; jj < test.numInstances(); jj++) {
                                            plotInstances.process(test.instance(jj), current, eval);
                                            if (outputPredictionsText) {
                                                classificationOutput.printClassification(current,
                                                        test.instance(jj), jj);
                                            }
                                        }
                                    }
                                }
                                if (outputPredictionsText) {
                                    classificationOutput.printFooter();
                                }
                                if (outputPredictionsText) {
                                    outBuff.append("\n");
                                }
                                if (inst.attribute(classIndex).isNominal()) {
                                    outBuff.append("=== Stratified cross-validation ===\n");
                                } else {
                                    outBuff.append("=== Cross-validation ===\n");
                                }
                                break;

                            case 2: // Percent split
                                if (!m_PreserveOrderBut.isSelected()) {
                                    m_Log.statusMessage("Randomizing instances...");
                                    try {
                                        rnd = Integer.parseInt(m_RandomSeedText.getText().trim());
                                    } catch (Exception ex) {
                                        m_Log.logMessage("Trouble parsing random seed value");
                                        rnd = 1;
                                    }
                                    inst.randomize(new Random(rnd));
                                }
                                int trainSize =
                                        (int) Math.round(inst.numInstances() * percent / 100);
                                int testSize = inst.numInstances() - trainSize;
                                Instances train = new Instances(inst, 0, trainSize);
                                Instances test = new Instances(inst, trainSize, testSize);
                                m_Log.statusMessage("Building model on training split ("
                                        + trainSize + " instances)...");
                                Classifier current = null;
                                try {
                                    current = AbstractClassifier.makeCopy(template);
                                    if (current instanceof weka.core.LogHandler) {
                                        ((weka.core.LogHandler) current).setLog(m_Log);
                                    }
                                } catch (Exception ex) {
                                    m_Log.logMessage("Problem copying classifier: "
                                            + ex.getMessage());
                                }
                                current.buildClassifier(train);
                                if (outputModelsForTrainingSplits) {
                                    outBuff.append("\n=== Classifier model for training split ("
                                            + trainSize + " instances) ===\n\n");
                                    outBuff.append(current.toString() + "\n");
                                }
                                eval = new Evaluation(train, costMatrix);

                                // make adjustments if the classifier is an InputMappedClassifier
                                eval =
                                        setupEval(eval, classifier, train, costMatrix, plotInstances,
                                                classificationOutput, false, collectPredictionsForEvaluation);
                                eval.setMetricsToDisplay(m_selectedEvalMetrics);

                                // plotInstances.setEvaluation(eval);
                                plotInstances.setUp();
                                m_Log.statusMessage("Evaluating on test split...");

                                if (outputPredictionsText) {
                                    printPredictionsHeader(outBuff, classificationOutput,
                                            "test split");
                                }

                                testTimeStart = System.currentTimeMillis();
                                if (classifier instanceof BatchPredictor
                                        && ((BatchPredictor) classifier)
                                        .implementsMoreEfficientBatchPrediction()) {
                                    Instances toPred = new Instances(test);
                                    for (int i = 0; i < toPred.numInstances(); i++) {
                                        toPred.instance(i).setClassMissing();
                                    }

                                    double[][] predictions =
                                            ((BatchPredictor) current).distributionsForInstances(toPred);
                                    plotInstances.process(test, predictions, eval);
                                    if (outputPredictionsText) {
                                        for (int jj = 0; jj < test.numInstances(); jj++) {
                                            classificationOutput.printClassification(predictions[jj],
                                                    test.instance(jj), jj);
                                        }
                                    }
                                } else {
                                    for (int jj = 0; jj < test.numInstances(); jj++) {
                                        plotInstances.process(test.instance(jj), current, eval);
                                        if (outputPredictionsText) {
                                            classificationOutput.printClassification(current,
                                                    test.instance(jj), jj);
                                        }
                                        if ((jj % 100) == 0) {
                                            m_Log.statusMessage("Evaluating on test split. Processed "
                                                    + jj + " instances...");
                                        }
                                    }
                                }
                                testTimeElapsed = System.currentTimeMillis() - testTimeStart;
                                if (outputPredictionsText) {
                                    classificationOutput.printFooter();
                                }
                                if (outputPredictionsText) {
                                    outBuff.append("\n");
                                }
                                outBuff.append("=== Evaluation on test split ===\n");
                                break;

                            case 4: // Test on user split
                                m_Log.statusMessage("Evaluating on test data...");
                                eval = new Evaluation(inst, costMatrix);
                                // make adjustments if the classifier is an InputMappedClassifier
                                eval =
                                        setupEval(eval, classifier, inst, costMatrix, plotInstances,
                                                classificationOutput, false, collectPredictionsForEvaluation);
                                plotInstances.setInstances(userTestStructure);
                                eval.setMetricsToDisplay(m_selectedEvalMetrics);

                                // plotInstances.setEvaluation(eval);
                                plotInstances.setUp();

                                if (outputPredictionsText) {
                                    printPredictionsHeader(outBuff, classificationOutput,
                                            "test set");
                                }

                                Instance instance;
                                int jj = 0;
                                Instances batchInst = null;
                                int batchSize = 100;
                                if (classifier instanceof BatchPredictor
                                        && ((BatchPredictor) classifier)
                                        .implementsMoreEfficientBatchPrediction()) {
                                    batchInst = new Instances(userTestStructure, 0);
                                    String batchSizeS =
                                            ((BatchPredictor) classifier).getBatchSize();
                                    if (batchSizeS != null && batchSizeS.length() > 0) {
                                        try {
                                            batchSizeS =
                                                    Environment.getSystemWide().substitute(batchSizeS);
                                        } catch (Exception ex) {
                                        }

                                        try {
                                            batchSize = Integer.parseInt(batchSizeS);
                                        } catch (NumberFormatException ex) {
                                            // just go with the default
                                        }
                                    }
                                    m_Log.logMessage("Performing batch prediction with batch size " + batchSize);
                                }
                                testTimeStart = System.currentTimeMillis();
                                while (source.hasMoreElements(userTestStructure)) {
                                    instance = source.nextElement(userTestStructure);

                                    if (classifier instanceof BatchPredictor
                                            && ((BatchPredictor) classifier)
                                            .implementsMoreEfficientBatchPrediction()) {
                                        batchInst.add(instance);
                                        if (batchInst.numInstances() == batchSize) {
                                            Instances toPred = new Instances(batchInst);
                                            for (int i = 0; i < toPred.numInstances(); i++) {
                                                toPred.instance(i).setClassMissing();
                                            }
                                            double[][] predictions =
                                                    ((BatchPredictor) classifier)
                                                            .distributionsForInstances(toPred);
                                            plotInstances.process(batchInst, predictions, eval);

                                            if (outputPredictionsText) {
                                                for (int kk = 0; kk < batchInst.numInstances(); kk++) {
                                                    classificationOutput.printClassification(
                                                            predictions[kk], batchInst.instance(kk), kk);
                                                }
                                            }
                                            jj += batchInst.numInstances();
                                            m_Log.statusMessage("Evaluating on test data. Processed "
                                                    + jj + " instances...");
                                            batchInst.delete();
                                        }
                                    } else {
                                        plotInstances.process(instance, classifier, eval);
                                        if (outputPredictionsText) {
                                            classificationOutput.printClassification(classifier,
                                                    instance, jj);
                                        }
                                        if ((++jj % 100) == 0) {
                                            m_Log.statusMessage("Evaluating on test data. Processed "
                                                    + jj + " instances...");
                                        }
                                    }
                                }

                                if (classifier instanceof BatchPredictor
                                        && ((BatchPredictor) classifier)
                                        .implementsMoreEfficientBatchPrediction()
                                        && batchInst.numInstances() > 0) {
                                    // finish the last batch

                                    Instances toPred = new Instances(batchInst);
                                    for (int i = 0; i < toPred.numInstances(); i++) {
                                        toPred.instance(i).setClassMissing();
                                    }

                                    double[][] predictions =
                                            ((BatchPredictor) classifier)
                                                    .distributionsForInstances(toPred);
                                    plotInstances.process(batchInst, predictions, eval);

                                    if (outputPredictionsText) {
                                        for (int kk = 0; kk < batchInst.numInstances(); kk++) {
                                            classificationOutput.printClassification(predictions[kk],
                                                    batchInst.instance(kk), kk);
                                        }
                                    }
                                }
                                testTimeElapsed = System.currentTimeMillis() - testTimeStart;

                                if (outputPredictionsText) {
                                    classificationOutput.printFooter();
                                }
                                if (outputPredictionsText) {
                                    outBuff.append("\n");
                                }
                                outBuff.append("=== Evaluation on test set ===\n");
                                break;

                            default:
                                throw new Exception("Test mode not implemented");
                        }

                        if (testMode != 1) {
                            String mode = "";
                            if (testMode == 2) {
                                mode = "test split";
                            } else if (testMode == 3) {
                                mode = "training data";
                            } else if (testMode == 4) {
                                mode = "supplied test set";
                            }
                            outBuff.append("\nTime taken to test model on " + mode + ": "
                                    + Utils.doubleToString(testTimeElapsed / 1000.0, 2)
                                    + " seconds\n\n");
                        }

                        if (outputSummary) {
                            outBuff.append(eval.toSummaryString(outputEntropy) + "\n");
                        }

                        if (inst.attribute(classIndex).isNominal()) {

                            if (outputPerClass) {
                                outBuff.append(eval.toClassDetailsString() + "\n");
                            }

                            if (outputConfusion) {
                                outBuff.append(eval.toMatrixString() + "\n");
                            }
                        }

                        if ((fullClassifier instanceof Sourcable)
                                && m_OutputSourceCode.isSelected()) {
                            outBuff.append("=== Source code ===\n\n");
                            outBuff.append(Evaluation.wekaStaticWrapper(
                                    ((Sourcable) fullClassifier), m_SourceCodeClass.getText()));
                        }

                        m_History.updateResult(name);
                        m_Log.logMessage("Finished " + cname);
                        m_Log.statusMessage("OK");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        m_Log.logMessage(ex.getMessage());
                        JOptionPane.showMessageDialog(ClassifierPanel.this,
                                "Problem evaluating classifier:\n" + ex.getMessage(),
                                "Evaluate classifier", JOptionPane.ERROR_MESSAGE);
                        m_Log.statusMessage("Problem evaluating classifier");
                    } finally {
                        try {
                            if (!saveVis && outputModel) {
                                ArrayList<Object> vv = new ArrayList<Object>();
                                vv.add(fullClassifier);
                                Instances trainHeader = new Instances(m_Instances, 0);
                                trainHeader.setClassIndex(classIndex);
                                vv.add(trainHeader);
                                if (grph != null) {
                                    vv.add(grph);
                                }
                                m_History.addObject(name, vv);
                            } else if (saveVis && plotInstances != null
                                    && plotInstances.canPlot(false)) {
                                // m_CurrentVis = new VisualizePanel();
                                VisualizePanel newVis = new VisualizePanel();
                                if (getMainApplication() != null) {
                                    Settings settings =
                                            getMainApplication().getApplicationSettings();
                                    newVis.applySettings(settings,
                                            weka.gui.explorer.VisualizePanel.ScatterDefaults.ID);
                                }
                                newVis.setName(name + " (" + inst.relationName() + ")");
                                newVis.setLog(m_Log);
                                newVis.addPlot(plotInstances.getPlotData(cname));
                                // m_CurrentVis.setColourIndex(plotInstances.getPlotInstances().classIndex()+1);
                                newVis.setColourIndex(plotInstances.getPlotInstances()
                                        .classIndex());
                                plotInstances.cleanUp();

                                ArrayList<Object> vv = new ArrayList<Object>();
                                if (outputModel) {
                                    vv.add(fullClassifier);
                                    Instances trainHeader = new Instances(m_Instances, 0);
                                    trainHeader.setClassIndex(classIndex);
                                    vv.add(trainHeader);
                                    if (grph != null) {
                                        vv.add(grph);
                                    }
                                }
                                vv.add(newVis);

                                if ((eval != null) && (eval.predictions() != null)) {
                                    vv.add(eval.predictions());
                                    vv.add(inst.classAttribute());
                                }
                                m_History.addObject(name, vv);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        if (isInterrupted()) {
                            m_Log.logMessage("Interrupted " + cname);
                            m_Log.statusMessage("Interrupted");
                        }

                        synchronized (this) {
                            m_StartBut.setEnabled(true);
                            m_StopBut.setEnabled(false);
                            m_RunThread = null;
                        }
                        if (m_Log instanceof TaskLogger) {
                            ((TaskLogger) m_Log).taskFinished();
                        }
                    }
                }
            };
            m_RunThread.setPriority(Thread.MIN_PRIORITY);
            m_RunThread.start();
        }
    }



}
