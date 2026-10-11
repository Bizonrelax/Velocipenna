package ru.bizonrelax.detector.model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Minimal valid PDF writer (Helvetica core font). Cyrillic glyphs cannot be
 * rendered with the standard 14 core fonts without an embedded TrueType font,
 * so non-Latin-1 characters are transliterated to Latin equivalents. This is a
 * documented compromise of dependency-free export (TZ-2.md).
 */
final class PdfWriter {

    private static final String[][] TRANSLIT = {
        {"а","a"},{"б","b"},{"в","v"},{"г","g"},{"д","d"},{"е","e"},{"ё","yo"},
        {"ж","zh"},{"з","z"},{"и","i"},{"й","y"},{"к","k"},{"л","l"},{"м","m"},
        {"н","n"},{"о","o"},{"п","p"},{"р","r"},{"с","s"},{"т","t"},{"у","u"},
        {"ф","f"},{"х","h"},{"ц","c"},{"ч","ch"},{"ш","sh"},{"щ","sch"},
        {"ъ","''"},{"ы","y"},{"ь","'"},{"э","e"},{"ю","yu"},{"я","ya"}
    };

    private PdfWriter() {
    }

    static String transliterate(String s) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            String rep = null;
            char lower = Character.toLowerCase(c);
            for (String[] pair : TRANSLIT) {
                if (pair[0].charAt(0) == lower) {
                    rep = pair[1];
                    break;
                }
            }
            if (rep != null) {
                out.append(Character.isUpperCase(c) ? capitalize(rep) : rep);
            } else if (c <= 0xFF) {
                out.append(c);
            } else {
                out.append('?');
            }
        }
        return out.toString();
    }

    private static String capitalize(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    static void write(String plainText, File target) throws IOException {
        List<String> lines = paginate(plainText);
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        List<Integer> offsets = new ArrayList<Integer>();

        // objects: 1 catalog, 2 pages, 3..N+2 page objects, N+3 content stream(s) merged into one
        int pageCount = lines.size() / 45 + (lines.size() % 45 == 0 ? 0 : 1);
        if (pageCount == 0) pageCount = 1;

        // Build a single content stream with page breaks
        StringBuilder content = new StringBuilder();
        int idx = 0;
        for (int p = 0; p < pageCount; p++) {
            content.append("BT\n/F1 10 Tf\n1 0 0 1 50 780 Tm\n14 TL\n");
            for (int row = 0; row < 45 && idx < lines.size(); row++, idx++) {
                content.append("(").append(escapePdf(lines.get(idx))).append(") Tj T*\n");
            }
            content.append("ET\n");
            if (p < pageCount - 1) content.append("\n");
        }
        byte[] contentBytes = content.toString().getBytes(StandardCharsets.ISO_8859_1);

        // Assemble PDF
        StringBuilder sb = new StringBuilder();
        offsets.add(sb.length()); // obj 1
        sb.append("1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");
        StringBuilder kids = new StringBuilder();
        for (int i = 0; i < pageCount; i++) {
            if (i > 0) kids.append(' ');
            kids.append(3 + i).append(" 0 R");
        }
        offsets.add(sb.length()); // obj 2
        sb.append("2 0 obj\n<< /Type /Pages /Kids [").append(kids)
          .append("] /Count ").append(pageCount).append(" >>\nendobj\n");
        for (int i = 0; i < pageCount; i++) {
            offsets.add(sb.length());
            sb.append((3 + i)).append(" 0 obj\n<< /Type /Page /Parent 2 0 R ")
              .append("/MediaBox [0 0 595 842] /Resources << /Font << /F1 ").append(3 + pageCount)
              .append(" 0 R >> >> /Contents ").append(3 + pageCount + 1).append(" 0 R >>\nendobj\n");
        }
        offsets.add(sb.length()); // font object
        sb.append((3 + pageCount)).append(" 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\nendobj\n");
        int contentObjNum = 3 + pageCount + 1;
        offsets.add(sb.length()); // content object
        sb.append(contentObjNum).append(" 0 obj\n<< /Length ").append(contentBytes.length)
          .append(" >>\nstream\n");

        byte[] header = "%PDF-1.4\n".getBytes(StandardCharsets.ISO_8859_1);
        byte[] pre = sb.toString().getBytes(StandardCharsets.ISO_8859_1);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(header);
        out.write(pre);
        out.write(contentBytes);
        String tail = "\nendstream\nendobj\n";
        int xrefStart = header.length + pre.length + contentBytes.length + tail.length();
        StringBuilder xref = new StringBuilder();
        xref.append(tail);
        xref.append("xref\n0 ").append(offsets.size() + 1).append("\n");
        xref.append("0000000000 65535 f \n");
        for (Integer off : offsets) {
            String s = String.format("%010d 00000 n \n", off + header.length);
            xref.append(s);
        }
        xref.append("trailer\n<< /Size ").append(offsets.size() + 1)
            .append(" /Root 1 0 R >>\nstartxref\n").append(xrefStart)
            .append("\n%%EOF\n");
        out.write(xref.toString().getBytes(StandardCharsets.ISO_8859_1));

        FileOutputStream fos = new FileOutputStream(target);
        try {
            fos.write(out.toByteArray());
        } finally {
            fos.close();
        }
    }

    private static List<String> paginate(String text) {
        List<String> lines = new ArrayList<String>();
        for (String raw : text.split("\r?\n", -1)) {
            String l = transliterate(raw);
            while (l.length() > 95) {
                lines.add(l.substring(0, 95));
                l = l.substring(95);
            }
            lines.add(l);
        }
        return lines;
    }

    private static String escapePdf(String s) {
        return s.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }
}
