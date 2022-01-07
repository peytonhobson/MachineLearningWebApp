package com.controller;

import com.Model.Response;
import com.output.Output;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.weka.classifiers.*;
import com.weka.classifiers.bayes.NaiveBayes;
import com.weka.classifiers.functions.Logistic;
import com.weka.classifiers.rules.ZeroR;
import static com.weka.classifiers.evaluation.Evaluation.evaluateModel;


/**
 * Class acts as Controller for Rest API
 */
@RestController
@RequestMapping("/api")
public class ResponseController {

    /**
     * Takes post request, mapped to '/inputData', performs classification
     * and returns response entity containing response with new output.
     * @param response
     * @return
     * @throws Exception
     */
    @PostMapping(path = "/inputData")
    public ResponseEntity<Response> classify(@RequestBody Response response) throws Exception {
        
        response.setOutput(startClassifier(response).replace("\n", "</br><br>"));
        return new ResponseEntity<Response>(response, HttpStatus.OK);
    }

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
