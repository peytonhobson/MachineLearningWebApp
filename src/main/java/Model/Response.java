package Model;

public class Response {

    private String dataset;
    private String classifier;

    public Response(String dataset, String classifier) {
        this.dataset = dataset;
        this.classifier = classifier;
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
                "dataset='" + dataset + '\'' +
                ", classifier='" + classifier + '\'' +
                '}';
    }
}




