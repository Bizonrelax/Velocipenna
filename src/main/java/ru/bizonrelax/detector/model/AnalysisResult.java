package ru.bizonrelax.detector.model;

/**
 * Immutable snapshot of Cyrillic analysis results for a single text document.
 */
public final class AnalysisResult {

    private final int totalChars;
    private final int cyrillicCount;
    private final int[] upperCounts;   // 33 slots, А..Я (Ё handled separately)
    private final int[] lowerCounts;   // 33 slots, а..я (ё handled separately)
    private final int yoUpper;
    private final int yoLower;
    private final int pureCyrillicWords;
    private final int mixedScriptWords;
    private final String mixedScriptReport;

    public AnalysisResult(int totalChars, int cyrillicCount,
                          int[] upperCounts, int[] lowerCounts,
                          int yoUpper, int yoLower,
                          int pureCyrillicWords, int mixedScriptWords,
                          String mixedScriptReport) {
        this.totalChars = totalChars;
        this.cyrillicCount = cyrillicCount;
        this.upperCounts = upperCounts;
        this.lowerCounts = lowerCounts;
        this.yoUpper = yoUpper;
        this.yoLower = yoLower;
        this.pureCyrillicWords = pureCyrillicWords;
        this.mixedScriptWords = mixedScriptWords;
        this.mixedScriptReport = mixedScriptReport;
    }

    public int getTotalChars() {
        return totalChars;
    }

    public int getCyrillicCount() {
        return cyrillicCount;
    }

    /** Density metric: ratio of Cyrillic chars to total length, in percent. */
    public double getDensityPercent() {
        if (totalChars == 0) {
            return 0.0;
        }
        return (cyrillicCount * 100.0) / totalChars;
    }

    public int getUpperCount(int index) {
        return upperCounts[index];
    }

    public int getLowerCount(int index) {
        return lowerCounts[index];
    }

    public int getYoUpper() {
        return yoUpper;
    }

    public int getYoLower() {
        return yoLower;
    }

    public int getPureCyrillicWords() {
        return pureCyrillicWords;
    }

    public int getMixedScriptWords() {
        return mixedScriptWords;
    }

    public String getMixedScriptReport() {
        return mixedScriptReport;
    }
}
