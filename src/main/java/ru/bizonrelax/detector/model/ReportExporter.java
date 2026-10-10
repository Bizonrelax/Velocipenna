package ru.bizonrelax.detector.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * Writes an analysis report to a UTF-8 text file. Pure logic, no Swing.
 */
public final class ReportExporter {

    private ReportExporter() {
    }

    /**
     * Exports the full plain-text report produced by MainWindow's rendering
     * (or any other caller) to the given file as UTF-8 with BOM so that
     * Windows Notepad displays Cyrillic correctly.
     */
    public static void export(String reportText, File target) throws IOException {
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(target), StandardCharsets.UTF_8));
        try {
            writer.write('\uFEFF'); // BOM for Notepad friendliness
            writer.write(reportText);
        } finally {
            writer.close();
        }
    }
}
