package edu.utah.blulab.controller;

import edu.utah.blulab.handlers.Encoder;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Controller {

    private static Logger logger = Logger.getLogger(Controller.class);
    public static void main(String[] args) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(Encoder.class.getResource("/data/Example_Fhx-ca_sentences.txt").getPath()));
            String line;
            String[] wordsArray;
            bufferedReader.readLine();
            String result = "\n\n";
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

            FileUtils.writeStringToFile(new File("FamilyHistoryFeatures.txt"), result,"UTF-8");

            bufferedReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
