package edu.utah.blulab.nlp;

import edu.utah.blulab.handlers.Encoder;
import opennlp.tools.ngram.NGramModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.StringList;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.lf5.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static edu.utah.blulab.utilities.BlulabUtilities.isNullOrEmpty;

public class FeaturePreprocessing {
    public static String getStringCase(String token) {
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

    public static String getWordsPrecedingToken(String rawText, String token) {
        String newText = rawText.replaceAll("-", " ");
        if (StringUtils.containsIgnoreCase(rawText, token) || (StringUtils.containsIgnoreCase(newText, token))) {
            int index = rawText.indexOf(token);
            if (index == -1)
                index = newText.indexOf(token);

            String output = rawText.substring(0, index);
            output = output.replaceAll("\\p{Punct}", " ");
            return output;
        } else
            return null;
    }


    public static String getStringPosition(String rawText, String token) {
        String newText = rawText.replaceAll("-", " ");
        if (StringUtils.containsIgnoreCase(rawText, token) || (StringUtils.containsIgnoreCase(newText, token))) {
            int index = rawText.indexOf(token);
            if (index == -1)
                index = newText.indexOf(token);

            int mark = rawText.length() / 3;

            if (index > 0 && index < mark)
                return "TOWARDS BEGINNING";
            else if (index > (mark * 2) && index < rawText.length() - 1)
                return "TOWARDS END";
            else
                return "TOWARDS MIDDLE";
        } else
            return null;
    }

    public static boolean isNumericContained(String token) {
        return token.matches(".*\\d+.*");
    }

    public static StringBuilder getNGrams(String subset) {
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


    public static StringBuilder getPOS(String subset) throws IOException {
        StringBuilder result = new StringBuilder();
        InputStream tokenModelIn = null;
        InputStream posModelIn = null;
        tokenModelIn = new FileInputStream(FeaturePreprocessing.class.getResource("/en-token.bin").getFile());
        TokenizerModel tokenModel = new TokenizerModel(tokenModelIn);
        Tokenizer tokenizer = new TokenizerME(tokenModel);
        String tokenList[] = tokenizer.tokenize(subset);

        // Parts-Of-Speech Tagging
        // reading parts-of-speech model to a stream
        posModelIn = new FileInputStream(FeaturePreprocessing.class.getResource("/en-pos-maxent.bin").getFile());

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
            result.append("\n").append(tokenList[i]).append("\t:\t").append(tags[i]).append("\t:\t").append(probs[i] * 100).append("%");
        }
        return result;

    }

    public static List<String> getPunctuationContent(String targetToken, String modifierToken, String rawText) {
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
                default:
                    break;
            }
        }
        List<String> result = new ArrayList<>();
        result.add(String.valueOf(count));
        if (count > 0)
            result.add(String.valueOf(punc));
        return result;
    }

    public static int getTokenCount(String targetToken, String modifierToken, String rawText) {
        String subset = getRawSubset(targetToken, modifierToken, rawText).get("subset");
        if (!isNullOrEmpty(subset))
            return (subset.trim().split(" ")).length;
        else
            return -1;
    }

    public static Map<String, String> getRawSubset(String targetToken, String modifierToken, String rawText) {
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

    public static List<String> getTokenList(String text, String delimiter) {
        String[] terms = text.split(delimiter);
        return new ArrayList<>(Arrays.asList(terms));
    }
}
