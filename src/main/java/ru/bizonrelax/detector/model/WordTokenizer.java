package ru.bizonrelax.detector.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Splits raw text into word tokens. A token is any maximal run of
 * characters that are not whitespace and not in the delimiter set.
 * Delimiters are kept out of tokens so classification stays accurate,
 * but punctuation-inside-word behaviour matches the specification:
 * a word containing Cyrillic plus anything else (Latin, digits, marks)
 * counts as "mixed".
 */
public final class WordTokenizer {

    private static final String DELIMITERS =
            " \t\r\n.,;:!?'\"()[]{}-–—…«»/\\@#$%^&*+=|<>~`_";

    private WordTokenizer() {
        // utility class
    }

    public static List<String> tokenize(String text) {
        List<String> words = new ArrayList<String>();
        if (text == null || text.isEmpty()) {
            return words;
        }
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (isDelimiter(c)) {
                if (current.length() > 0) {
                    words.add(current.toString());
                    current.setLength(0);
                }
            } else {
                current.append(c);
            }
        }
        if (current.length() > 0) {
            words.add(current.toString());
        }
        return words;
    }

    private static boolean isDelimiter(char c) {
        if (DELIMITERS.indexOf(c) >= 0) {
            return true;
        }
        int type = Character.getType(c);
        return type == Character.SPACE_SEPARATOR
                || type == Character.LINE_SEPARATOR
                || type == Character.PARAGRAPH_SEPARATOR;
    }
}
