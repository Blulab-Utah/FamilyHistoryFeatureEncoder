package edu.utah.blulab.controller;

import edu.utah.blulab.services.IFeatureEncoder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.View;

import java.io.File;
import java.io.IOException;


@Controller
public class FeatureEncoderController {

    private static final Logger LOGGER = Logger.getLogger(FeatureEncoderController.class);

    @Autowired
    private IFeatureEncoder featureEncoder;
    @Autowired
    private View jsonView;

    @RequestMapping(value = "/getFeatures", method = RequestMethod.POST, produces="text/plain")
    @ResponseBody
    public String getFeatures(@RequestParam(value = "inputFile") MultipartFile[] inputFiles) throws Exception {

        File inputFile = null;
        for (MultipartFile file : inputFiles) {
            if (!file.isEmpty()) {
                if (file.getOriginalFilename().split("\\.")[1].equals("txt")) {
                    inputFile = new File(file.getOriginalFilename());
                    try {
                        file.transferTo(inputFile);
                    } catch (IOException e) {
                        return e.getMessage();
                    }
                }
            }
        }

        return featureEncoder.getEncodedFeatures(inputFile);

    }
}
