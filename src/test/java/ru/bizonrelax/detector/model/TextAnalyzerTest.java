package ru.bizonrelax.detector.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit tests for the pure analysis pipeline.
 * Note: some test strings intentionally mix Cyrillic and Latin homoglyphs.
 */
public class TextAnalyzerTest {

    private final TextAnalyzer analyzer = new TextAnalyzer();

    @Test
    public void countsAllThirtyThreeLettersIncludingYo() {
        // Homoglyph trap: Cyrillic "Р" (U+0420) followed by Latin o, t.
        String mixed = "\u0420" + "o" + "t"; // Cyrillic Р + Latin o,t
        assertTrue(CyrillicCharSet.isCyrillicLetter('\u0420'));
        assertFalse(CyrillicCharSet.isLatinLetter('\u0420'));
        AnalysisResult r = analyzer.analyze(mixed);
        assertEquals(3, r.getTotalChars());
        assertEquals(1, r.getCyrillicCount());
        assertEquals(1, r.getMixedScriptWords());
        assertEquals(0, r.getPureCyrillicWords());
        assertTrue(r.getMixedScriptReport().contains(mixed));
    }

    @Test
    public void pureCyrillicTextIsFullyCounted() {
        String text = "Привет, мир! Ёлка мала.";
        AnalysisResult r = analyzer.analyze(text);
        // letters: Привет(6) мир(3) Ёлка(4) мала(4) = 17 cyrillic letters
        assertEquals(17, r.getCyrillicCount());
        assertEquals(1, r.getYoUpper());
        assertEquals(0, r.getYoLower());
        assertEquals(4, r.getPureCyrillicWords()); // Привет, мир, Ёлка, мала
        assertEquals(0, r.getMixedScriptWords());
    }

    @Test
    public void upperAndLowerTalliesAreSeparated() {
        String text = "Аа Бв";
        AnalysisResult r = analyzer.analyze(text);
        int aIdx = CyrillicCharSet.indexOfUpper('А');
        assertEquals(1, r.getUpperCount(aIdx));
        assertEquals(1, r.getLowerCount(aIdx));
        int bIdx = CyrillicCharSet.indexOfUpper('Б');
        assertEquals(1, r.getUpperCount(bIdx));
        int vIdx = CyrillicCharSet.indexOfUpper('В');
        assertEquals(1, r.getLowerCount(vIdx));
    }

    @Test
    public void densityIsComputed() {
        String text = "abcде"; // 5 chars, 2 cyrillic
        AnalysisResult r = analyzer.analyze(text);
        assertEquals(5, r.getTotalChars());
        assertEquals(2, r.getCyrillicCount());
        assertEquals(40.0, r.getDensityPercent(), 0.0001);
    }

    @Test
    public void emptyTextYieldsZeroes() {
        AnalysisResult r = analyzer.analyze("");
        assertEquals(0, r.getTotalChars());
        assertEquals(0.0, r.getDensityPercent(), 0.0);
        assertEquals("Mixed-script anomalies: none detected.", r.getMixedScriptReport());
    }
}
