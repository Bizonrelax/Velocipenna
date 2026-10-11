package ru.bizonrelax.detector.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Persistence gateway for the whole AppConfig. One file, atomic-ish write.
 * Kept separate from the value object to avoid a god-class.
 */
public final class ConfigStore {

    public static final String DEFAULT_FILE_NAME = "velocipenna.properties";

    private final File configFile;

    public ConfigStore() {
        this(new File(System.getProperty("user.home"), DEFAULT_FILE_NAME));
    }

    public ConfigStore(File configFile) {
        this.configFile = configFile;
    }

    public File getConfigFile() {
        return configFile;
    }

    public AppConfig load() {
        Properties props = new Properties();
        InputStream in = null;
        try {
            if (configFile.isFile()) {
                in = new FileInputStream(configFile);
                props.load(in);
            }
        } catch (IOException ignored) {
            // corrupted config falls back to defaults
        } finally {
            closeQuietly(in);
        }
        return AppConfig.fromProperties(props);
    }

    public void save(AppConfig config) {
        Properties props = config.toProperties();
        OutputStream out = null;
        try {
            out = new FileOutputStream(configFile);
            props.store(out, "Velocipenna application config");
        } catch (IOException e) {
            System.err.println("Could not save config: " + e.getMessage());
        } finally {
            closeQuietly(out);
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
