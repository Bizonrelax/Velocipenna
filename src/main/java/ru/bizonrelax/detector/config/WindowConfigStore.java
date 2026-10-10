package ru.bizonrelax.detector.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Standalone persistence of window geometry and user preferences.
 * Stored as .properties in the user home directory.
 */
public final class WindowConfigStore {

    private static final String FILE_NAME = "velocipenna.properties";

    private final File configFile;

    public WindowConfigStore() {
        this(new File(System.getProperty("user.home"), FILE_NAME));
    }

    public WindowConfigStore(File configFile) {
        this.configFile = configFile;
    }

    public WindowState load() {
        Properties props = new Properties();
        InputStream in = null;
        try {
            if (configFile.isFile()) {
                in = new FileInputStream(configFile);
                props.load(in);
            }
        } catch (IOException ignored) {
            // corrupted or unreadable config falls back to defaults
        } finally {
            closeQuietly(in);
        }
        int width = getInt(props, "window.width", 900);
        int height = getInt(props, "window.height", 650);
        int x = getInt(props, "window.x", -1);
        int y = getInt(props, "window.y", -1);
        int fontSize = getInt(props, "font.size", 14);
        return new WindowState(width, height, x, y, fontSize);
    }

    public void save(WindowState state) {
        Properties props = new Properties();
        props.setProperty("window.width", String.valueOf(state.getWidth()));
        props.setProperty("window.height", String.valueOf(state.getHeight()));
        props.setProperty("window.x", String.valueOf(state.getX()));
        props.setProperty("window.y", String.valueOf(state.getY()));
        props.setProperty("font.size", String.valueOf(state.getFontSize()));
        OutputStream out = null;
        try {
            out = new FileOutputStream(configFile);
            props.store(out, "Velocipenna window state");
        } catch (IOException e) {
            System.err.println("Could not save config: " + e.getMessage());
        } finally {
            closeQuietly(out);
        }
    }

    private int getInt(Properties props, String key, int def) {
        String raw = props.getProperty(key);
        if (raw == null) {
            return def;
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private void closeQuietly(java.io.Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException ignored) {
                // nothing meaningful to do here
            }
        }
    }
}
