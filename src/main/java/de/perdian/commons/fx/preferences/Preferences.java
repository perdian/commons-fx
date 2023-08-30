package de.perdian.commons.fx.preferences;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * A set of JavaFX properties backed by preferences file.
 *
 * Whenever the value of a property that has been created via this {@code Preferences} instance changes
 * the complete set of properties are stored back to the preferences file.
 *
 * @author Christian Seifert
 */

public class Preferences {

    private static final Logger log = LoggerFactory.getLogger(Preferences.class);

    private Map<String, StringProperty> properties = new HashMap<>();
    private Path storagePath = null;

    /**
     * Create a new {@code Preferences} instance backed by the {@code Path} given as parameter
     *
     * @param storagePath
     *      the {@code Path} to be used as backing file. When creating the {@code Preferences}
     *      instance, any values stored within the backing file will be read and stored into this
     *      {@code Preferences} instance. Whenever one of the preferences inside this
     *      {@code Preferences} object change, the complete set of preferences will be written
     *      back into the backing file.
     *      Any error that occurs while reading from or writing into the backing file will be
     *      ignored so that any user of the {@code Preferences} instance will be able to continue
     *      working with the preferences themselves no matter what happens with the backing file.
     *
     */
    public Preferences(Path storagePath) {
        this.setStoragePath(storagePath);
        this.setProperties(new HashMap<>());
        this.readPropertiesFromBackingFile();
    }

    public synchronized <T> ObjectProperty<T> getObjectProperty(String propertyName, StringConverter<T> converter) {
        return this.getObjectProperty(propertyName, null, converter);
    }

    public synchronized <T> ObjectProperty<T> getObjectProperty(String propertyName, T defaultValue, StringConverter<T> converter) {
        StringProperty stringProperty = this.getStringProperty(propertyName, () -> converter.toString(defaultValue));
        ObjectProperty<T> objectProperty = new SimpleObjectProperty<>(converter.fromString(stringProperty.getValue()));
        stringProperty.addListener((o, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue)) {
                T newObject = converter.fromString(newValue);
                if (!Objects.equals(newObject, objectProperty.getValue())) {
                    objectProperty.setValue(newObject);
                }
            }
        });
        objectProperty.addListener((o, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue)) {
                String newString = converter.toString(newValue);
                if (!Objects.equals(newString, stringProperty.getValue())) {
                    stringProperty.setValue(newString);
                }
            }
        });
        return objectProperty;
    }

    public synchronized StringProperty getStringProperty(String propertyName) {
        return this.getStringProperty(propertyName, () -> null);
    }

    public synchronized StringProperty getStringProperty(String propertyName, String defaultValue) {
        return this.getStringProperty(propertyName, () -> defaultValue);
    }

    public synchronized StringProperty getStringProperty(String propertyName, Supplier<String> defaultValueSupplier) {
        StringProperty stringProperty = this.getProperties().get(propertyName);
        if (stringProperty == null) {
            stringProperty = new SimpleStringProperty(defaultValueSupplier.get());
            stringProperty.addListener((o, oldValue, newValue) -> this.writePropertiesToBackingFile());
            this.getProperties().put(propertyName, stringProperty);
        }
        return stringProperty;
    }

    private void readPropertiesFromBackingFile() {
        try {
            if (Files.exists(this.getStoragePath())) {
                log.debug("Reading preferences from: {}", this.getStoragePath());
                try (InputStream sourceStream = new GZIPInputStream(new BufferedInputStream(Files.newInputStream(this.getStoragePath())))) {
                    Properties properties = new Properties();
                    properties.loadFromXML(sourceStream);
                    properties.entrySet().stream()
                        .map(entry -> Map.entry((String)entry.getKey(), (String)entry.getValue()))
                        .filter(entry -> entry.getKey() != null && !entry.getKey().isEmpty())
                        .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
                        .forEach(entry -> this.getStringProperty(entry.getKey(), entry.getValue()));
                }
            }
        } catch (Exception e) {
            log.warn("Cannot read preferences from: {}", this.getStoragePath(), e);
        }
    }

    private void writePropertiesToBackingFile() {
        log.debug("Writing preferences into: {}", this.getStoragePath());
        try (OutputStream outStream = new GZIPOutputStream(new BufferedOutputStream(Files.newOutputStream(this.getStoragePath(), StandardOpenOption.CREATE)))) {
            Properties targetProperties = new Properties();
            properties.forEach((key, value) -> targetProperties.setProperty(key, value.getValue()));
            targetProperties.storeToXML(outStream, null);
        } catch (Exception e) {
            log.warn("Cannot write preferences into: {}", this.getStoragePath(), e);
        }
    }

    private Path getStoragePath() {
        return this.storagePath;
    }
    private void setStoragePath(Path storagePath) {
        this.storagePath = storagePath;
    }

    private Map<String, StringProperty> getProperties() {
        return this.properties;
    }
    private void setProperties(Map<String, StringProperty> properties) {
        this.properties = properties;
    }

}
