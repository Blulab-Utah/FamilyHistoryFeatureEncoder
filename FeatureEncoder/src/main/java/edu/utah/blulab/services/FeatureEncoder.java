package edu.utah.blulab.services;

import edu.utah.blulab.handlers.Encoder;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import org.apache.log4j.Logger;



@Service
public class FeatureEncoder implements IFeatureEncoder {
    private static final Logger logger = Logger.getLogger(FeatureEncoder.class);

    @Override
    public String getEncodedFeatures(File input) throws Exception {
        String result = "\n\n";
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(input));
            String line;
            String[] wordsArray;
            bufferedReader.readLine();
            result += "\n-----------------------FAMILY HISTORY FEATURES---------------------\n\n";

            while (true) {

                line = bufferedReader.readLine();
                if (line == null) {
                    break;
                } else {
                    wordsArray = line.split("\t");
                    String target = wordsArray[1];
                    String modifier = wordsArray[2];
                    String rawText = wordsArray[3];
                    result += "\nFilename: "+wordsArray[0];
                    result += Encoder.getEncodedFeatures(target,modifier,rawText);
                    result += "\n_________________________________________________________________________________\n";
                    logger.info(result);
                }
            }

            bufferedReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
