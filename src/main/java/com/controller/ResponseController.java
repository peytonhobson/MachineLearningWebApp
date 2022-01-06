package com.controller;

import com.Model.Response;
import com.output.Output;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class ResponseController {

    @PostMapping(path = "/inputData")
    public Response classify(@RequestBody Response response) throws Exception {

        response.setOutput(Output.startClassifier(response));
        return response;
    }
}
