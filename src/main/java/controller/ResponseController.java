package controller;

import Model.Response;
import com.web.Output;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
@CrossOrigin
public class ResponseController {

    private static final Logger logger = LogManager.getLogger(ResponseController.class);

    @PostMapping(path = "/inputData")
    public ResponseEntity<String> response(@RequestBody Response response) throws Exception {

        logger.info("Calling classification");

        String str = Output.startClassifier(response);
        return new ResponseEntity<>(str, HttpStatus.OK);
    }
}
