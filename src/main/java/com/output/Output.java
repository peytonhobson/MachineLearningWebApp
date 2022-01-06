package com.output;

import com.Model.Response;
import com.weka.classifiers.Classifier;
import com.weka.classifiers.bayes.NaiveBayes;
import com.weka.classifiers.functions.Logistic;
import com.weka.classifiers.rules.ZeroR;

import static com.weka.classifiers.evaluation.Evaluation.evaluateModel;

public class Output {

    public static String startClassifier(Response response) throws Exception {

        String[] args = new String[2];
        args[0] = "-t";
        args[1] = "https://storm.cis.fordham.edu/~gweiss/data-mining/weka-data/" + response.getDataset() + ".arff";

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

        return evaluateModel(classifier, args);
    }
}