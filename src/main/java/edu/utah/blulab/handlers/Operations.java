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

public class Operations {

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

        String wordsProceedingTarget = getWordsProceedingToken(rawText.toLowerCase().trim(), targetToken.toLowerCase().trim());
        if (!BlulabUtilities.isNullOrEmpty(wordsProceedingTarget))
            result.append("\nWords Proceeding Target: ").append(wordsProceedingTarget);
        String wordsProceedingModifier = getWordsProceedingToken(rawText.toLowerCase().trim(), modifierToken.toLowerCase().trim());
        if (!BlulabUtilities.isNullOrEmpty(wordsProceedingModifier))
            result.append("\nWords Proceeding Modifier: ").append(wordsProceedingModifier);

        String targetCase = getStringCase(target.trim());
        String modifierCase = getStringCase(modifier.trim());
        result.append("\nTarget Case: ").append(targetCase);
        result.append("\nModifier Case: ").append(modifierCase);
        result.append("\n");
        result.append("\n").append("==================================").append("\n").append("STATISTICS");

        result.append("\n\nN GRAM RESULTS: ");
        StringBuilder nGramsResult = getNGrams(subset);
        result.append("\n").append(nGramsResult);


        StringBuilder posOutput = getPOS(subset);
        result.append("\nPOS TAGGING RESULTS: ");
        result.append("\n").append(posOutput);
        result.append("\n");
        result.append("\n");
        return result.toString();
    }

    private static String getStringCase(String token) {
        StringBuilder uppercaseCharacters = new StringBuilder();
        for (int i = 0; i < token.length(); i++) {
            char ch = token.charAt(i);
            if (Character.isUpperCase(ch)) {
                uppercaseCharacters.append(ch);
            }
        }

        StringBuilder lowercaseCharacters = new StringBuilder();
        for (int i = 0; i < token.length(); i++) {
            char ch = token.charAt(i);
            if (Character.isLowerCase(ch)) {
                lowercaseCharacters.append(ch);
            }
        }
        if ((uppercaseCharacters.length() == 0) && (lowercaseCharacters.length() != 0))
            return "LOWERCASE";
        else if ((uppercaseCharacters.length() != 0) && (lowercaseCharacters.length() == 0))
            return "UPPERCASE";
        else
            return "MIXED";
    }

    private static String getWordsProceedingToken(String rawText, String token) {
        String newText = rawText.replaceAll("-", " ");
        if (StringUtils.containsIgnoreCase(rawText, token) || (StringUtils.containsIgnoreCase(newText, token))) {
            int index = rawText.indexOf(token);
            if (index == -1)
                index = newText.indexOf(token);

            String output = rawText.substring(index + token.length(), rawText.length() - 1);
            output = output.replaceAll("\\p{Punct}", " ");
            return output;
        } else
            return null;
    }


    private static String getStringPosition(String rawText, String token) {
        String newText = rawText.replaceAll("-", " ");
        if (StringUtils.containsIgnoreCase(rawText, token) || (StringUtils.containsIgnoreCase(newText, token))) {
            int index = rawText.indexOf(token);
            if (index == -1)
                index = newText.indexOf(token);

            int mark = rawText.length() / 3;

            if (index > 0 && index < mark)
                return "UPPER";
            else if (index > (mark * 2) && index < rawText.length() - 1)
                return "LOWER";
            else
                return "MIDDLE";
        } else
            return null;
    }

    private static StringBuilder getNGrams(String subset) {
        StringBuilder result = new StringBuilder();
        StringList tokens = new StringList(WhitespaceTokenizer.INSTANCE.tokenize(subset));
        result.append("\nTokens in the subset: ").append(tokens);

        NGramModel nGramModel = new NGramModel();
        nGramModel.add(tokens, 1, 3);

        result.append("\nTotal ngrams: ").append(nGramModel.numberOfGrams());
        for (StringList ngram : nGramModel) {
            result.append("\n").append(nGramModel.getCount(ngram)).append(" - ").append(ngram);
        }
        result.append("\n");
        return result;
    }


    private static StringBuilder getPOS(String subset) throws IOException {
        StringBuilder result = new StringBuilder();
        InputStream tokenModelIn = null;
        InputStream posModelIn = null;
        tokenModelIn = new FileInputStream(Operations.class.getResource("/bin/en-token.bin").getPath());
        TokenizerModel tokenModel = new TokenizerModel(tokenModelIn);
        Tokenizer tokenizer = new TokenizerME(tokenModel);
        String tokenList[] = tokenizer.tokenize(subset);

        // Parts-Of-Speech Tagging
        // reading parts-of-speech model to a stream
        posModelIn = new FileInputStream(Operations.class.getResource("/bin/en-pos-maxent.bin").getPath());

        // loading the parts-of-speech model from stream
        POSModel posModel = new POSModel(posModelIn);

        // initializing the parts-of-speech tagger with model
        POSTaggerME posTagger = new POSTaggerME(posModel);

        // Tagger tagging the tokens
        String tags[] = posTagger.tag(tokenList);

        // Getting the probabilities of the tags given to the tokens
        double probs[] = posTagger.probs();

        result.append("\nToken\t:\tTag\t:\tProbability\n---------------------------------------------");
        for (int i = 0; i < tokenList.length; i++) {
            result.append("\n").append(tokenList[i]).append("\t:\t").append(tags[i]).append("\t:\t").append(probs[i]);
        }
        return result;

    }

    private static List<String> getPunctuationContent(String targetToken, String modifierToken, String rawText) {
        String subset = getRawSubset(targetToken, modifierToken, rawText).get("subset");
        StringBuilder punc = new StringBuilder();
        int count = 0;
        for (int i = 0; i < subset.length(); i++) {
            char ch = subset.charAt(i);
            switch (ch) {
                case ',':
                    count++;
                    punc.append(", ");
                    break;
                case ';':
                    count++;
                    punc.append("; ");
                    break;
                case ':':
                    count++;
                    punc.append(": ");
                    break;
                case '.':
                    count++;
                    punc.append(". ");
                    break;
                case '?':
                    count++;
                    punc.append("? ");
                    break;
                case '!':
                    count++;
                    punc.append("! ");
                    break;
                case '-':
                    count++;
                    punc.append("- ");
                    break;
                case '\'':
                    count++;
                    punc.append("\' ");
                    break;
            }
        }
        List<String> result = new ArrayList<>();
        result.add(String.valueOf(count));
        if (count > 0)
            result.add(String.valueOf(punc));
        return result;
    }

    private static int getTokenCount(String targetToken, String modifierToken, String rawText) {
        String subset = getRawSubset(targetToken, modifierToken, rawText).get("subset");
        if (null != subset)
            return subset.length();
        else
            return -1;
    }

    private static Map<String, String> getRawSubset(String targetToken, String modifierToken, String rawText) {
        String newText = rawText.replaceAll("-", " ");
        Map<String, String> subsetMap = new HashMap<>();

        String subset = null;
        if (isContentExist(targetToken, modifierToken, rawText)) {
            int targetIndex = rawText.indexOf(targetToken);
            if (targetIndex == -1)
                targetIndex = newText.indexOf(targetToken);
            int modifierIndex = rawText.indexOf(modifierToken);
            int newIndex = -1;
            if (modifierIndex == -1)
                modifierIndex = newText.indexOf(modifierToken);
            if (targetIndex > modifierIndex) {
                newIndex = modifierIndex + modifierToken.length();
                subset = rawText.substring(newIndex, targetIndex);
                subsetMap.put("Order", "Backward");
            } else {
                newIndex = targetIndex + targetToken.length();
                subset = rawText.substring(newIndex, modifierIndex);
                subsetMap.put("Order", "Forward");

            }
        }
        subsetMap.put("subset", subset);
        return subsetMap;
    }

    private static boolean isContentExist(String targetToken, String modifierToken, String rawText) {
        String newText = rawText.replaceAll("-", " ");


        return (StringUtils.containsIgnoreCase(rawText, targetToken) || (StringUtils.containsIgnoreCase(newText, targetToken)))
                && (StringUtils.containsIgnoreCase(rawText, modifierToken) || (StringUtils.containsIgnoreCase(newText, modifierToken)));
    }

    private static String getToken(String text, String delimiter) {
        String[] terms = text.split(delimiter);
        return (terms[terms.length - 1]).trim();
    }
}
