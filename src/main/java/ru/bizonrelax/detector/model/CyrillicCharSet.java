package ru.bizonrelax.detector.model;

/**
 * Pure character validation logic, free from any UI dependency.
 * Covers the full Russian alphabet: 33 letters plus Ё/ё.
 */
public final class CyrillicCharSet {

    /** А-Я without Ё — 32 letters, used for indexed tallies. */
    public static final char[] UPPER_BASE = {
        'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М',
        'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ',
        'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я'
    };

    public static final char[] LOWER_BASE = {
        'а', 'б', 'в', 'г', 'д', 'е', 'ж', 'з', 'и', 'й', 'к', 'л', 'м',
        'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ',
        'ъ', 'ы', 'ь', 'э', 'ю', 'я'
    };

    public static final char YO_UPPER = 'Ё';
    public static final char YO_LOWER = 'ё';

    private static final int BASE_SIZE = UPPER_BASE.length; // 32

    private CyrillicCharSet() {
        // utility class
    }

    public static int baseSize() {
        return BASE_SIZE;
    }

    public static boolean isCyrillicLetter(char c) {
        if (c == YO_UPPER || c == YO_LOWER) {
            return true;
        }
        return indexOfUpper(c) >= 0 || indexOfLower(c) >= 0;
    }

    /** @return index in А-Я (minus Ё) or -1 if not an uppercase base letter. */
    public static int indexOfUpper(char c) {
        for (int i = 0; i < BASE_SIZE; i++) {
            if (UPPER_BASE[i] == c) {
                return i;
            }
        }
        return -1;
    }

    /** @return index in а-я (minus ё) or -1 if not a lowercase base letter. */
    public static int indexOfLower(char c) {
        for (int i = 0; i < BASE_SIZE; i++) {
            if (LOWER_BASE[i] == c) {
                return i;
            }
        }
        return -1;
    }

    /** Basic Latin check sufficient for homoglyph detection in words. */
    public static boolean isLatinLetter(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }
}
