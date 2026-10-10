package ru.bizonrelax.detector.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for charset detection (UTF-8 vs cp1251) and report export.
 */
public class CharsetDetectorTest {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private File write(String name, String text, Charset cs) throws Exception {
        File f = tmp.newFile(name);
        Writer w = new OutputStreamWriter(new FileOutputStream(f), cs);
        try {
            w.write(text);
        } finally {
            w.close();
        }
        return f;
    }

    @Test
    public void detectsUtf8() throws Exception {
        File f = write("u1.txt", "Привет, мир!", StandardCharsets.UTF_8);
        assertEquals(StandardCharsets.UTF_8, CharsetDetector.detect(f));
    }

    @Test
    public void detectsCp1251() throws Exception {
        File f = write("w1.txt", "Привет, мир! Ёлка.", CharsetDetector.CP1251);
        assertEquals(CharsetDetector.CP1251, CharsetDetector.detect(f));
    }

    @Test
    public void detectsUtf8WithBom() throws Exception {
        File f = tmp.newFile("bom.txt");
        FileOutputStream out = new FileOutputStream(f);
        try {
            out.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
            Writer w = new OutputStreamWriter(out, StandardCharsets.UTF_8);
            w.write("текст");
            w.flush();
        } finally {
            out.close();
        }
        assertEquals(StandardCharsets.UTF_8, CharsetDetector.detect(f));
    }

    @Test
    public void asciiOnlyIsTreatedAsCp1251AndAnalyzedConsistently() throws Exception {
        File f = write("ascii.txt", "hello world", StandardCharsets.UTF_8);
        TextAnalyzer analyzer = new TextAnalyzer();
        AnalysisResult r = analyzer.analyzeFileAutoDetect(f);
        // pure ASCII is indistinguishable: detector falls back to cp1251,
        // which decodes ASCII identically — counts must be correct anyway
        assertEquals("windows-1251", r.getCharsetName());
        assertEquals(0, r.getCyrillicCount());
    }

    @Test
    public void cp1251FileYieldsSameCountsAsUtf8String() throws Exception {
        String text = "Рот вёл счёт";
        File utf = write("u2.txt", text, StandardCharsets.UTF_8);
        File win = write("w2.txt", text, CharsetDetector.CP1251);
        TextAnalyzer analyzer = new TextAnalyzer();
        AnalysisResult a = analyzer.analyzeFileAutoDetect(utf);
        AnalysisResult b = analyzer.analyzeFileAutoDetect(win);
        assertEquals(a.getTotalChars(), b.getTotalChars());
        assertEquals(a.getCyrillicCount(), b.getCyrillicCount());
        assertEquals("UTF-8", a.getCharsetName());
        assertEquals("windows-1251", b.getCharsetName());
    }

    @Test
    public void exportWritesReadableUtf8WithBom() throws Exception {
        File target = tmp.newFile("report.txt");
        String report = "Отчёт\nКириллица OK";
        ReportExporter.export(report, target);
        byte[] raw = java.nio.file.Files.readAllBytes(target.toPath());
        assertTrue("BOM expected",
                (raw[0] & 0xFF) == 0xEF && (raw[1] & 0xFF) == 0xBB && (raw[2] & 0xFF) == 0xBF);
        String back = new String(raw, 3, raw.length - 3, StandardCharsets.UTF_8);
        assertEquals(report, back);
    }
}
