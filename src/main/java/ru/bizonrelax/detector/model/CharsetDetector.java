package ru.bizonrelax.detector.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Detects the encoding of a text file: UTF-8 (with or without BOM) or
 * Windows-1251 (cp1251). Pure logic, no Swing dependencies.
 */
public final class CharsetDetector {

    /** Strict cp1251 as used by Windows Cyrillic documents. */
    public static final Charset CP1251 = Charset.forName("windows-1251");

    private CharsetDetector() {
    }

    /**
     * Reads the beginning of the file and decides which charset to use.
     * Rules:
     *  - EF BB BF prefix -> UTF-8 with BOM
     *  - valid strict UTF-8 multi-byte sequences containing Cyrillic
     *    (UTF-8 always encodes U+0410..U+044F as D0/D1 xx) -> UTF-8
     *  - otherwise -> windows-1251
     * Note: plain ASCII is indistinguishable between the two encodings,
     * so it is treated as cp1251 (a safe superset for Russian texts).
     */
    public static Charset detect(File file) throws IOException {
        byte[] head = readHead(file, 64 * 1024);
        if (head.length >= 3
                && (head[0] & 0xFF) == 0xEF
                && (head[1] & 0xFF) == 0xBB
                && (head[2] & 0xFF) == 0xBF) {
            return StandardCharsets.UTF_8;
        }
        if (looksLikeUtf8Cyrillic(head)) {
            return StandardCharsets.UTF_8;
        }
        return CP1251;
    }

    /**
     * True if the sample contains at least one well-formed UTF-8 two-byte
     * sequence in the Cyrillic block range (lead byte D0 or D1 followed by
     * a continuation byte 80..BF). This distinguishes UTF-8 Russian text
     * from the same text encoded in cp1251 (where those bytes are letters).
     */
    static boolean looksLikeUtf8Cyrillic(byte[] sample) {
        for (int i = 0; i + 1 < sample.length; i++) {
            int b0 = sample[i] & 0xFF;
            int b1 = sample[i + 1] & 0xFF;
            if ((b0 == 0xD0 || b0 == 0xD1) && b1 >= 0x80 && b1 <= 0xBF) {
                return true;
            }
        }
        return false;
    }

    private static byte[] readHead(File file, int maxBytes) throws IOException {
        FileInputStream in = new FileInputStream(file);
        try {
            byte[] buf = new byte[maxBytes];
            int total = 0;
            while (total < maxBytes) {
                int n = in.read(buf, total, maxBytes - total);
                if (n < 0) {
                    break;
                }
                total += n;
            }
            if (total == maxBytes) {
                return buf;
            }
            byte[] copy = new byte[total];
            System.arraycopy(buf, 0, copy, 0, total);
            return copy;
        } finally {
            in.close();
        }
    }
}
