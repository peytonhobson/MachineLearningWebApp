export class Response{
    public dataset: string;
    public classifier: string;

    constructor(dataset: string, classifier: string){
        this.dataset = dataset;
        this.classifier = classifier;
    }
}