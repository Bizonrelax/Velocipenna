package ru.bizonrelax.detector.model;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * Report rendering and multi-format export. Pure logic, no Swing.
 * Supported formats (TZ-2.md): txt, md, html, xml, csv, tsv, pdf, docx.
 * CSV/TSX open directly in MS Excel. PDF and DOCX are produced as valid
 * minimal files without external libraries.
 */
public final class ReportExporter {

    public enum Format {
        TXT("txt"), MD("md"), HTML("html"), XML("xml"),
        CSV("csv"), TSV("tsv"), PDF("pdf"), DOCX("docx");

        private final String extension;

        Format(String extension) {
            this.extension = extension;
        }

        public String getExtension() {
            return extension;
        }

        /** Case-insensitive lookup; unknown or blank -> TXT. */
        public static Format fromName(String name) {
            if (name == null) {
                return TXT;
            }
            String n = name.trim().toLowerCase(Locale.ROOT).replace(".", "");
            for (Format f : values()) {
                if (f.extension.equals(n)) {
                    return f;
                }
            }
            if ("htm".equals(n)) return HTML;
            if ("markdown".equals(n) || "mdown".equals(n)) return MD;
            if ("xls".equals(n) || "excel".equals(n)) return CSV;
            return TXT;
        }
    }

    private ReportExporter() {
    }

    /** Plain-text export, UTF-8 with BOM (Notepad friendly). */
    public static void export(String reportText, File target) throws IOException {
        export(reportText, target, Format.TXT);
    }

    public static void export(String reportText, File target, Format format) throws IOException {
        byte[] data;
        switch (format) {
            case HTML: data = utf8Bom(toHtml(reportText)); break;
            case XML:  data = utf8Bom(toXml(reportText)); break;
            case MD:   data = utf8Bom(toMarkdown(reportText)); break;
            case CSV:  data = ("\uFEFF" + delimited(reportText, ',')).getBytes(java.nio.charset.StandardCharsets.UTF_8); break;
            case TSV:  data = ("\uFEFF" + delimited(reportText, '\t')).getBytes(java.nio.charset.StandardCharsets.UTF_8); break;
            case PDF:  PdfWriter.write(stripMarkup(reportText), target); return;
            case DOCX: DocxWriter.write(stripMarkup(reportText), target); return;
            case TXT:
            default:   data = utf8Bom(reportText); break;
        }
        writeBytes(data, target);
    }

    /** Size of the produced file for statistics accounting. */
    public static long exportedSize(File target) {
        return target.isFile() ? target.length() : 0L;
    }

    private static byte[] utf8Bom(String s) {
        return ("\uFEFF" + s).getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    private static void writeBytes(byte[] data, File target) throws IOException {
        java.io.FileOutputStream out = new java.io.FileOutputStream(target);
        try {
            out.write(data);
        } finally {
            out.close();
        }
    }

    private static String escapeHtml(String s) {
        StringBuilder sb = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '&': sb.append("&amp;"); break;
                case '<': sb.append("&lt;"); break;
                case '>': sb.append("&gt;"); break;
                default: sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String toHtml(String reportText) {
        String[] lines = reportText.split("\r?\n", -1);
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n<html lang=\"ru\">\n<head>\n<meta charset=\"UTF-8\">\n");
        sb.append("<title>Velocipenna report</title>\n");
        sb.append("<style>body{font-family:'Courier New',monospace;background:#fff;color:#000;}");
        sb.append("pre{white-space:pre-wrap;}</style>\n</head>\n<body>\n");
        sb.append("<h1>Velocipenna</h1>\n<pre>\n");
        for (String line : lines) {
            sb.append(escapeHtml(line)).append('\n');
        }
        sb.append("</pre>\n</body>\n</html>\n");
        return sb.toString();
    }

    public static String toXml(String reportText) {
        String[] lines = reportText.split("\r?\n", -1);
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<report app=\"Velocipenna\">\n");
        for (String line : lines) {
            int colon = line.indexOf(':');
            if (colon > 0 && colon < 60 && !line.startsWith("  ")) {
                String key = line.substring(0, colon).trim();
                String value = line.substring(colon + 1).trim();
                if (key.matches("[A-Za-z\u0410-\u044F\u0401\u0451][\\w .-]*")) {
                    sb.append("  <entry key=\"").append(escapeHtml(key)).append("\">")
                      .append(escapeHtml(value)).append("</entry>\n");
                    continue;
                }
            }
            sb.append("  <line>").append(escapeHtml(line)).append("</line>\n");
        }
        sb.append("</report>\n");
        return sb.toString();
    }

    public static String toMarkdown(String reportText) {
        String[] lines = reportText.split("\r?\n", -1);
        StringBuilder sb = new StringBuilder();
        sb.append("# Velocipenna report\n\n```\n");
        for (String line : lines) {
            sb.append(line).append('\n');
        }
        sb.append("```\n");
        return sb.toString();
    }

    /** Two-column delimited table (line number, content) — opens in MS Excel. */
    public static String delimited(String reportText, char delimiter) {
        String[] lines = reportText.split("\r?\n", -1);
        StringBuilder sb = new StringBuilder();
        sb.append("Line").append(delimiter).append("Content").append("\r\n");
        for (int i = 0; i < lines.length; i++) {
            sb.append(i + 1).append(delimiter).append(csvCell(lines[i], delimiter)).append("\r\n");
        }
        return sb.toString();
    }

    private static String csvCell(String s, char delimiter) {
        boolean needsQuotes = s.indexOf(delimiter) >= 0 || s.indexOf('"') >= 0
                || s.indexOf('\n') >= 0 || s.indexOf('\r') >= 0;
        String v = s.replace("\"", "\"\"");
        return needsQuotes ? "\"" + v + "\"" : v;
    }

    private static String stripMarkup(String reportText) {
        return reportText.replace("\uFEFF", "");
    }
}
