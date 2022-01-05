package com.web;

import Model.Response;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.rules.ZeroR;

public class Output {

    public static String startClassifier(Response response) throws Exception {

        // TODO: Add way to differentiate between datasets for classIndex

        String[] args = new String[2];
        args[0] = "-t";
        args[1] = "https://storm.cis.fordham.edu/~gweiss/data-mining/weka-data" + response.getDataset() + ".arff";

        Classifier classifier;

        if (response.getClassifier().equals("Naive Bayes")) {
            classifier = new NaiveBayes();
        }
        else if(response.getClassifier().equals("ZeroR")) {
            classifier = new ZeroR();
        }
        else {
            classifier = new Logistic();
        }

//        try {
//            ConverterUtils.DataSource source = new ConverterUtils.DataSource(args[1]);
//        }
//        catch(Exception exception) {
//            exception.printStackTrace();
//            System.exit(1);
//        }

        return Evaluation.evaluateModel(classifier, args);
    }
}