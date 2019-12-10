package de.perdian.commons.fx.preferences;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used to create new {@code Preferences} instances that are bound to an underlying file on the
 * local filesystem.
 *
 * @author Christian Seifert
 */

public class PreferencesBuilder {

    private static final Logger log = LoggerFactory.getLogger(PreferencesBuilder.class);

    private Path path = null;

    public Preferences buildPreferences() {
        Path path = this.getPath();
        if (path == null) {
            throw new IllegalArgumentException("Property 'path' must not be null");
        } else if (!Files.exists(path.getParent())) {
            try {
                log.debug("Creating new preferences directory at: {}", this.getPath().getParent());
                Files.createDirectories(this.getPath().getParent());
            } catch (IOException e) {
                throw new IllegalArgumentException("Cannot create preferences directory at: " + this.getPath(), e);
            }
        }
        Map<String, String> properties = new HashMap<>(PreferencesBuilder.loadPropertiesFromPath(path));
        Preferences preferences = new Preferences(properties);
        preferences.addPreferencesListener((key, oldValue, newValue) -> PreferencesBuilder.storePropertiesToPath(properties, path));
        return preferences;
    }

    private static Map<String, String> loadPropertiesFromPath(Path sourcePath) {
        Map<String, String> resultMap = new HashMap<>();
        if (Files.exists(sourcePath)) {
            log.info("Loading preferences from: {}", sourcePath);
            try {
                try (InputStream sourceStream = new GZIPInputStream(new BufferedInputStream(Files.newInputStream(sourcePath)))) {
                    Properties properties = new Properties();
                    properties.loadFromXML(sourceStream);
                    properties.entrySet().stream()
                        .map(entry -> Map.entry((String)entry.getKey(), (String)entry.getValue()))
                        .filter(entry -> StringUtils.isNotEmpty(entry.getKey()) && StringUtils.isNotEmpty(entry.getValue()))
                        .forEach(entry -> resultMap.put(entry.getKey(), entry.getValue()));
                }
            } catch (Exception e) {
                log.warn("Cannot load properties from path: {}", sourcePath, e);
            }
        }
        return resultMap;
    }

    private static void storePropertiesToPath(Map<String, String> properties, Path targetPath) {
        log.info("Storing preferences into: {}", targetPath);
        Properties targetProperties = new Properties();
        properties.forEach((key, value) -> targetProperties.setProperty(key, value));
        try (OutputStream outStream = new GZIPOutputStream(new BufferedOutputStream(Files.newOutputStream(targetPath, StandardOpenOption.CREATE)))) {
            targetProperties.storeToXML(outStream, null);
        } catch (Exception e) {
            log.warn("Cannot store properties into path: {}", targetPath, e);
        }
    }

    public PreferencesBuilder path(Path path) {
        this.setPath(path);
        return this;
    }
    private Path getPath() {
        return this.path;
    }
    private void setPath(Path path) {
        this.path = path;
    }

}
