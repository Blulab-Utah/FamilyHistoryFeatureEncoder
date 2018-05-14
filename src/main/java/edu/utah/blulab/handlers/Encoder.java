package edu.utah.blulab.handlers;

import edu.utah.blulab.utilities.BlulabUtilities;

import java.io.*;
import java.util.List;
import java.util.Map;

import static edu.utah.blulab.nlp.FeaturePreprocessing.*;

public class Encoder {

    public static String getEncodedFeatures(String target, String modifier, String rawText) throws IOException {


        List<String> targetTokenList = getTokenList(target,":");
        List<String> modifierTokenList = getTokenList(modifier,":");


        String targetTokenValue = targetTokenList.get(targetTokenList.size()-1);
        String modifierTokenValue = modifierTokenList.get(modifierTokenList.size()-1);
        StringBuilder result = new StringBuilder("\nTarget Semantic Type : " +targetTokenList.get(0).toLowerCase());
        result.append("\nTarget Value: ").append(targetTokenValue.toLowerCase());
        result.append("\nModifier Semantic Type : ").append(modifierTokenList.get(0).toLowerCase());
        result.append("\nModifier Value : ").append(modifierTokenValue.toLowerCase());
        result.append("\nNumber of Targets: ").append(targetTokenList.size()/2);
        result.append("\nNumber of Modifiers: ").append(modifierTokenList.size()/2);

        int numberOfTokens = getTokenCount(targetTokenValue.toLowerCase().trim(), modifierTokenValue.toLowerCase().trim(), rawText.toLowerCase().trim());
        List<String> getPunctuationContent = getPunctuationContent(targetTokenValue.trim(), modifierTokenValue.trim(), rawText.toLowerCase().trim());
        Map<String, String> subsetMap = getRawSubset(targetTokenValue.trim(), modifierTokenValue.trim(), rawText.toLowerCase().trim());
        String subset = subsetMap.get("subset");

        result.append("\nContent between target and modifier: ").append(subset);
        result.append("\nNumber of Tokens between target and modifier: ").append(numberOfTokens);
        result.append("\nNumber of Punctuations between target and modifier: ").append(getPunctuationContent.get(0));
        if (getPunctuationContent.size() > 1)
            result.append("\nPunctuations between target and modifier: ").append(getPunctuationContent.get(1));
        result.append("\nOrder from target to modifier: ").append(subsetMap.get("Order"));

        String targetPosition = getStringPosition(rawText.toLowerCase().trim(), targetTokenValue.toLowerCase().trim());
        if (null != targetPosition)
            result.append("\nTarget Position: ").append(targetPosition);
        String modifierPosition = getStringPosition(rawText.toLowerCase().trim(), modifierTokenValue.toLowerCase().trim());
        if (null != modifierPosition)
            result.append("\nModifier Position: ").append(modifierPosition);

        String wordsPrecedingTarget = getWordsPrecedingToken(rawText.toLowerCase().trim(), targetTokenValue.toLowerCase().trim());
        if (!BlulabUtilities.isNullOrEmpty(wordsPrecedingTarget))
            result.append("\nWords Preceding Target: ").append(wordsPrecedingTarget);
        String wordsPrecedingModifier = getWordsPrecedingToken(rawText.toLowerCase().trim(), modifierTokenValue.toLowerCase().trim());
        if (!BlulabUtilities.isNullOrEmpty(wordsPrecedingModifier))
            result.append("\nWords Preceding Modifier: ").append(wordsPrecedingModifier);

        boolean targetNumericCheck = isNumericContained(targetTokenValue);
        boolean modifierNumericCheck = isNumericContained(modifierTokenValue);
        result.append("\nTarget Numeric Content: ").append(targetNumericCheck);
        result.append("\nModifier Numeric Content: ").append(modifierNumericCheck);

        String targetCase = getStringCase(targetTokenValue.trim());
        String modifierCase = getStringCase(modifierTokenValue.trim());
        result.append("\nTarget Case: ").append(targetCase);
        result.append("\nModifier Case: ").append(modifierCase);
        result.append("\n");
        result.append("\n").append("==================================").append("\n").append("STATISTICS");

        result.append("\n\nN gram results of the subset between target and modifier: ");
        StringBuilder nGramsResult = getNGrams(rawText);
        result.append("\n").append(nGramsResult);


        StringBuilder posOutput = getPOS(rawText);
        result.append("\nPOS tagging results of the subset between target and modifier: ");
        result.append("\n").append(posOutput);
        result.append("\n");
        result.append("\n");
        return result.toString();
    }


}


