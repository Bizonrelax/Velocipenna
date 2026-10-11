package ru.bizonrelax.detector.view;

import java.awt.Color;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Luminance helpers for automatic text-color inversion (TZ.md item 5.2).
 * Formula fixed by spec: Y = 0.299R + 0.587G + 0.114B; if Y < 128 -> white text.
 */
public final class ContrastUtil {

    private ContrastUtil() {
    }

    public static double luminance(Color c) {
        return 0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue();
    }

    /** Returns black or white, whichever is readable on the given background. */
    public static Color readableTextColor(Color background) {
        return luminance(background) < 128 ? Color.WHITE : Color.BLACK;
    }

    /** True when the given color would make default black text unreadable. */
    public static boolean needsInversion(Color background) {
        return luminance(background) < 128;
    }
}
