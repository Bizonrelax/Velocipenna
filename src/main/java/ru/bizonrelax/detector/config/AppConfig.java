package ru.bizonrelax.detector.config;

import java.io.File;
import java.util.Properties;

/**
 * Mutable snapshot of ALL persisted application settings:
 * window geometry, fonts, colors, paths and long-term statistics.
 * Immutable value objects WindowState remains for geometry only.
 */
public final class AppConfig {

    private int width = 900;
    private int height = 650;
    private int x = -1;
    private int y = -1;
    private int fontSize = 14;

    // background color handling (TZ.md item 5)
    /** true = RGB spinners mode, false = palette mode. */
    private boolean bgSpinnerMode = true;
    private int bgR = 255;
    private int bgG = 255;
    private int bgB = 255;

    // remembered directories (TZ.md items 1 and 3)
    private String lastInputDir = System.getProperty("user.home");
    private String lastExportDir = System.getProperty("user.home");

    // extended window text field (TZ.md Dop-1)
    private String extText = "";
    private int extTextFontSize = 14;
    private int extUiFontSize = 14;

    // UI language code: ru, en, de, es, zh (TZ.md Dop-8)
    private String language = "ru";

    // long-term statistics (TZ.md Dop-10)
    private String firstLaunchDate = "";      // ISO local date-time string
    private long totalRunTimeSeconds = 0L;
    private long launchCount = 0L;
    private long totalAnalyzedChars = 0L;
    private long totalExportedBytes = 0L;
    private long totalOpenedFiles = 0L;

    public static AppConfig fromProperties(Properties p) {
        AppConfig c = new AppConfig();
        c.width = getInt(p, "window.width", c.width);
        c.height = getInt(p, "window.height", c.height);
        c.x = getInt(p, "window.x", c.x);
        c.y = getInt(p, "window.y", c.y);
        c.fontSize = clamp(getInt(p, "font.size", c.fontSize), 8, 48);
        c.bgSpinnerMode = !"palette".equalsIgnoreCase(p.getProperty("bg.mode", "spinner"));
        c.bgR = clamp(getInt(p, "bg.r", c.bgR), 0, 255);
        c.bgG = clamp(getInt(p, "bg.g", c.bgG), 0, 255);
        c.bgB = clamp(getInt(p, "bg.b", c.bgB), 0, 255);
        c.lastInputDir = nonBlank(p.getProperty("last.input.dir"), c.lastInputDir);
        c.lastExportDir = nonBlank(p.getProperty("last.export.dir"), c.lastExportDir);
        c.extText = p.getProperty("ext.text", "");
        c.extTextFontSize = clamp(getInt(p, "ext.text.font.size", c.extTextFontSize), 8, 48);
        c.extUiFontSize = clamp(getInt(p, "ext.ui.font.size", c.extUiFontSize), 8, 48);
        c.language = nonBlank(p.getProperty("language"), c.language);
        c.firstLaunchDate = p.getProperty("stats.first.launch", "");
        c.totalRunTimeSeconds = getLong(p, "stats.total.runtime.seconds", 0L);
        c.launchCount = getLong(p, "stats.launch.count", 0L);
        c.totalAnalyzedChars = getLong(p, "stats.total.analyzed.chars", 0L);
        c.totalExportedBytes = getLong(p, "stats.total.exported.bytes", 0L);
        c.totalOpenedFiles = getLong(p, "stats.total.opened.files", 0L);
        return c;
    }

    public Properties toProperties() {
        Properties p = new Properties();
        p.setProperty("window.width", String.valueOf(width));
        p.setProperty("window.height", String.valueOf(height));
        p.setProperty("window.x", String.valueOf(x));
        p.setProperty("window.y", String.valueOf(y));
        p.setProperty("font.size", String.valueOf(fontSize));
        p.setProperty("bg.mode", bgSpinnerMode ? "spinner" : "palette");
        p.setProperty("bg.r", String.valueOf(bgR));
        p.setProperty("bg.g", String.valueOf(bgG));
        p.setProperty("bg.b", String.valueOf(bgB));
        p.setProperty("last.input.dir", lastInputDir);
        p.setProperty("last.export.dir", lastExportDir);
        p.setProperty("ext.text", extText);
        p.setProperty("ext.text.font.size", String.valueOf(extTextFontSize));
        p.setProperty("ext.ui.font.size", String.valueOf(extUiFontSize));
        p.setProperty("language", language);
        p.setProperty("stats.first.launch", firstLaunchDate);
        p.setProperty("stats.total.runtime.seconds", String.valueOf(totalRunTimeSeconds));
        p.setProperty("stats.launch.count", String.valueOf(launchCount));
        p.setProperty("stats.total.analyzed.chars", String.valueOf(totalAnalyzedChars));
        p.setProperty("stats.total.exported.bytes", String.valueOf(totalExportedBytes));
        p.setProperty("stats.total.opened.files", String.valueOf(totalOpenedFiles));
        return p;
    }

    private static int getInt(Properties p, String key, int def) {
        try {
            return Integer.parseInt(p.getProperty(key, String.valueOf(def)).trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private static long getLong(Properties p, String key, long def) {
        try {
            return Long.parseLong(p.getProperty(key, String.valueOf(def)).trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private static int clamp(int v, int min, int max) {
        return Math.min(max, Math.max(min, v));
    }

    private static String nonBlank(String s, String def) {
        if (s == null || s.trim().isEmpty()) {
            return def;
        }
        return s;
    }

    public File configDirectory() {
        return new File(System.getProperty("user.home"));
    }

    // --- getters / setters ---

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getX() { return x; }
    public int getY() { return y; }
    public void setGeometry(int w, int h, int px, int py) {
        this.width = Math.max(320, w);
        this.height = Math.max(240, h);
        this.x = px;
        this.y = py;
    }

    public int getFontSize() { return fontSize; }
    public void setFontSize(int v) { this.fontSize = clamp(v, 8, 48); }

    public boolean isBgSpinnerMode() { return bgSpinnerMode; }
    public void setBgSpinnerMode(boolean v) { this.bgSpinnerMode = v; }
    public int getBgR() { return bgR; }
    public int getBgG() { return bgG; }
    public int getBgB() { return bgB; }
    public void setBackgroundRgb(int r, int g, int b) {
        this.bgR = clamp(r, 0, 255);
        this.bgG = clamp(g, 0, 255);
        this.bgB = clamp(b, 0, 255);
    }

    public String getLastInputDir() { return lastInputDir; }
    public void setLastInputDir(String v) { this.lastInputDir = v; }
    public String getLastExportDir() { return lastExportDir; }
    public void setLastExportDir(String v) { this.lastExportDir = v; }

    public String getExtText() { return extText; }
    public void setExtText(String v) { this.extText = v == null ? "" : v; }
    public int getExtTextFontSize() { return extTextFontSize; }
    public void setExtTextFontSize(int v) { this.extTextFontSize = clamp(v, 8, 48); }
    public int getExtUiFontSize() { return extUiFontSize; }
    public void setExtUiFontSize(int v) { this.extUiFontSize = clamp(v, 8, 48); }

    public String getLanguage() { return language; }
    public void setLanguage(String v) { this.language = v; }

    public String getFirstLaunchDate() { return firstLaunchDate; }
    public void setFirstLaunchDate(String v) { this.firstLaunchDate = v; }
    public long getTotalRunTimeSeconds() { return totalRunTimeSeconds; }
    public void setTotalRunTimeSeconds(long v) { this.totalRunTimeSeconds = Math.max(0, v); }
    public long getLaunchCount() { return launchCount; }
    public void setLaunchCount(long v) { this.launchCount = Math.max(0, v); }
    public long getTotalAnalyzedChars() { return totalAnalyzedChars; }
    public void setTotalAnalyzedChars(long v) { this.totalAnalyzedChars = Math.max(0, v); }
    public long getTotalExportedBytes() { return totalExportedBytes; }
    public void addExportedBytes(long v) { this.totalExportedBytes += Math.max(0, v); }
    public long getTotalOpenedFiles() { return totalOpenedFiles; }
    public void incrementOpenedFiles() { this.totalOpenedFiles++; }
}
