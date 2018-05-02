package edu.utah.blulab.handlers;

import edu.utah.blulab.utilities.BlulabUtilities;
import opennlp.tools.ngram.NGramModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.*;
import opennlp.tools.util.StringList;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.utah.blulab.utilities.BlulabUtilities.*;

public class EncoderOperations {

    public static String getEncodedFeatures(String target, String modifier, String rawText) throws IOException {

        String targetToken = getToken(target, ":");

        String modifierToken = getToken(modifier, ":");

        StringBuilder result = null;
        int numberOfTokens = getTokenCount(targetToken.toLowerCase().trim(), modifierToken.toLowerCase().trim(), rawText.toLowerCase().trim());
        List<String> getPunctuationContent = getPunctuationContent(targetToken.trim(), modifierToken.trim(), rawText.toLowerCase().trim());
        Map<String, String> subsetMap = getRawSubset(targetToken.trim(), modifierToken.trim(), rawText.toLowerCase().trim());
        String subset = subsetMap.get("subset");

        result = new StringBuilder("\nContent between target and modifier: " + subset);
        result.append("\nNumber of Tokens between target and modifier: ").append(numberOfTokens);
        result.append("\nNumber of Punctuations between target and modifier: ").append(getPunctuationContent.get(0));
        if (getPunctuationContent.size() > 1)
            result.append("\nPunctuations between target and modifier: ").append(getPunctuationContent.get(1));
        result.append("\nOrder from target to modifier: ").append(subsetMap.get("Order"));

        String targetPosition = getStringPosition(rawText.toLowerCase().trim(), targetToken.toLowerCase().trim());
        if (null != targetPosition)
            result.append("\nTarget Position: ").append(targetPosition);
        String modifierPosition = getStringPosition(rawText.toLowerCase().trim(), modifierToken.toLowerCase().trim());
        if (null != modifierPosition)
            result.append("\nModifier Position: ").append(modifierPosition);

        String wordsPrecedingTarget = getWordsPrecedingToken(rawText.toLowerCase().trim(), targetToken.toLowerCase().trim());
        if (!BlulabUtilities.isNullOrEmpty(wordsPrecedingTarget))
            result.append("\nWords Preceding Target: ").append(wordsPrecedingTarget);
        String wordsPrecedingModifier = getWordsPrecedingToken(rawText.toLowerCase().trim(), modifierToken.toLowerCase().trim());
        if (!BlulabUtilities.isNullOrEmpty(wordsPrecedingModifier))
            result.append("\nWords Preceding Modifier: ").append(wordsPrecedingModifier);

        String targetCase = getStringCase(target.trim());
        String modifierCase = getStringCase(modifier.trim());
        result.append("\nTarget Case: ").append(targetCase);
        result.append("\nModifier Case: ").append(modifierCase);
        result.append("\n");
        result.append("\n").append("==================================").append("\n").append("STATISTICS");

        result.append("\n\nN gram results of the subset between target and modifier: ");
        StringBuilder nGramsResult = getNGrams(subset);
        result.append("\n").append(nGramsResult);


        StringBuilder posOutput = getPOS(subset);
        result.append("\nPOS tagging results of the subset between target and modifier: ");
        result.append("\n").append(posOutput);
        result.append("\n");
        result.append("\n");
        return result.toString();
    }
}


