package edu.utah.blulab.controller;

import edu.utah.blulab.handlers.EncoderOperations;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Controller {

    private static Logger logger = Logger.getLogger(Controller.class);
    public static void main(String[] args) {
        try {
            BufferedReader buf = new BufferedReader(new FileReader(EncoderOperations.class.getResource("/data/Example_Fhx-ca_sentences.txt").getPath()));
            String lineJustFetched;
            String[] wordsArray;
            buf.readLine();
            String result = "\n\n";
            result += "\n-----------------------FAMILY HISTORY FEATURES---------------------\n\n";

            while (true) {

                lineJustFetched = buf.readLine();
                if (lineJustFetched == null) {
                    break;
                } else {
                    wordsArray = lineJustFetched.split("\t");
                    String target = wordsArray[1];
                    String modifier = wordsArray[2];
                    String rawText = wordsArray[3];
                    result += "\nFilename: "+wordsArray[0];
                    result += "\nTarget: " +target;
                    result += "\nModifier: " +modifier;
                    result += EncoderOperations.getEncodedFeatures(target,modifier,rawText);
                    result += "\n_________________________________________________________________________________\n";
                    logger.info(result);
                }
            }

            FileUtils.writeStringToFile(new File("FamilyHistoryFeatures.txt"), result,"UTF-8");

            buf.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
