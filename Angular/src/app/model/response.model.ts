/**
 * Class used to represent response, where output is initialized to empty
 */
 export class Response{
    public dataset: string;
    public classifier: string;
    public output : string;

    constructor(dataset: string, classifier: string){
        this.dataset = dataset;
        this.classifier = classifier;
        this.output = "";
    }
}