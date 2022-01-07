package com.Model;

public class Response {

    /**
     * String containing dataset to evaluate
     */
    private String dataset;

    /**
     * Classifier used for evaluation
     */
    private String classifier;

    /**
     * Output from classification
     */
    private String output;

    /**
     * Full constructor
     * @param dataset
     * @param classifier
     * @param output
     */
    public Response(String dataset, String classifier, String output) {
        this.dataset = dataset;
        this.classifier = classifier;
        this.output = output;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    @Override
    public String toString() {
        return "Response{" +
                "dataset:'" + dataset + '\'' +
                ", classifier='" + classifier + '\'' +
                ", com.output='" + output + '\'' +
                '}';
    }
}




