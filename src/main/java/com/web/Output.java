package com.web;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import static weka.classifiers.evaluation.Evaluation.evaluateModel;

public class Output {

    protected Instances m_Instances;

    protected void startClassifier() throws Exception {

        StringBuffer outBuff = new StringBuffer();
        DataSource source = new DataSource("https://storm.cis.fordham.edu/~gweiss/data-mining/weka-data/weather.arff");

        // TODO: Add way to differentiate between datasets for classIndex

        String[] args = new String[2];
        args[0] = "-t";
        args[1] = "https://storm.cis.fordham.edu/~gweiss/data-mining/weka-data/weather.arff";

        Classifier classifier = new NaiveBayes();

        System.out.println(evaluateModel(classifier, args));

    }
}