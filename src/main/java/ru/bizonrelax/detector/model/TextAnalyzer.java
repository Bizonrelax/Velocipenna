package ru.bizonrelax.detector.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Pure text analysis pipeline. No Swing/AWT imports allowed in this package.
 */
public final class TextAnalyzer {

    private static final String WORD_DELIMITERS = " \t\r\n.,;:!?\"'()[]{}-–—…«»/\\@#$%^&*+=|<>~`";

    /**
     * Reads a file with the given charset and analyzes its content.
     */
    public AnalysisResult analyzeFile(File file, Charset charset) throws IOException {
        String content = read(file, charset);
        AnalysisResult base = analyze(content);
        return new AnalysisResult(base.getTotalChars(), base.getCyrillicCount(),
                base.getUpperCountsArray(), base.getLowerCountsArray(),
                base.getYoUpper(), base.getYoLower(),
                base.getPureCyrillicWords(), base.getMixedScriptWords(),
                base.getMixedScriptReport(), charset.name());
    }

    /**
     * Detects the encoding (UTF-8 or cp1251), reads and analyzes the file.
     */
    public AnalysisResult analyzeFileAutoDetect(File file) throws IOException {
        return analyzeFile(file, CharsetDetector.detect(file));
    }

    private String read(File file, Charset charset) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), charset));
        try {
            char[] buf = new char[8192];
            int read;
            while ((read = reader.read(buf)) != -1) {
                sb.append(buf, 0, read);
            }
        } finally {
            reader.close();
        }
        // strip UTF-8 BOM if present
        if (sb.length() > 0 && sb.charAt(0) == '\uFEFF') {
            sb.deleteCharAt(0);
        }
        return sb.toString();
    }

    /**
     * Core analysis over an in-memory string.
     */
    public AnalysisResult analyze(String text) {
        int size = CyrillicCharSet.baseSize();
        int[] upper = new int[size];
        int[] lower = new int[size];
        int yoUpper = 0;
        int yoLower = 0;
        int cyrillicTotal = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == CyrillicCharSet.YO_UPPER) {
                yoUpper++;
                cyrillicTotal++;
            } else if (c == CyrillicCharSet.YO_LOWER) {
                yoLower++;
                cyrillicTotal++;
            } else {
                int ui = CyrillicCharSet.indexOfUpper(c);
                if (ui >= 0) {
                    upper[ui]++;
                    cyrillicTotal++;
                } else {
                    int li = CyrillicCharSet.indexOfLower(c);
                    if (li >= 0) {
                        lower[li]++;
                        cyrillicTotal++;
                    }
                }
            }
        }

        int pureWords = 0;
        int mixedWords = 0;
        List<String> mixedList = new ArrayList<String>();

        for (String word : splitWords(text)) {
            boolean hasCyr = false;
            boolean hasLatin = false;
            for (int i = 0; i < word.length(); i++) {
                char c = word.charAt(i);
                if (CyrillicCharSet.isCyrillicLetter(c)) {
                    hasCyr = true;
                } else if (CyrillicCharSet.isLatinLetter(c)) {
                    hasLatin = true;
                }
            }
            if (hasCyr && !hasLatin) {
                pureWords++;
            } else if (hasCyr && hasLatin) {
                mixedWords++;
                if (mixedList.size() < 500) { // cap report size
                    mixedList.add(word);
                }
            }
        }

        String report = buildReport(mixedList, mixedWords);
        return new AnalysisResult(text.length(), cyrillicTotal, upper, lower,
                yoUpper, yoLower, pureWords, mixedWords, report);
    }

    private List<String> splitWords(String text) {
        List<String> words = new ArrayList<String>();
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

    private boolean isDelimiter(char c) {
        if (WORD_DELIMITERS.indexOf(c) >= 0) {
            return true;
        }
        return Character.getType(c) == Character.SPACE_SEPARATOR;
    }

    private String buildReport(List<String> mixedList, int mixedWords) {
        if (mixedList.isEmpty()) {
            return "Mixed-script anomalies: none detected.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Found ").append(mixedWords).append(" mixed Cyrillic/Latin word(s):\n");
        for (String w : mixedList) {
            sb.append("  - ").append(w).append('\n');
        }
        if (mixedWords > mixedList.size()) {
            sb.append("  ... (report truncated at 500 entries)\n");
        }
        return sb.toString();
    }
}
