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
        this(defaultConfigFile());
    }

    /** Settings live in the subfolder ~/Velo (user request). */
    public static File defaultConfigFile() {
        File dir = new File(System.getProperty("user.home"), "Velo");
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }
        return new File(dir, DEFAULT_FILE_NAME);
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
        // one-time migration: old flat file in the home directory root
        if (!configFile.isFile()) {
            File legacy = new File(System.getProperty("user.home"), DEFAULT_FILE_NAME);
            if (legacy.isFile()) {
                try {
                    migrateLegacyFile(legacy, configFile);
                    in = new FileInputStream(configFile);
                    props.load(in);
                } catch (IOException ignored2) {
                    // stay on defaults
                } finally {
                    closeQuietly(in);
                }
            }
        }
        return AppConfig.fromProperties(props);
    }

    static void migrateLegacyFile(File from, File to) throws IOException {
        java.nio.file.Files.copy(from.toPath(), to.toPath(),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }

    public void save(AppConfig config) {
        Properties props = config.toProperties();
        OutputStream out = null;
        try {
            File parent = configFile.getParentFile();
            if (parent != null && !parent.isDirectory()) {
                parent.mkdirs();
            }
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
