package de.perdian.commons.fx.preferences;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.StringConverter;

/**
 * A set of preferences and properties, backed by preferences file.
 *
 * Whenever a property within the preferences is changed, the underlying file will be updated so that
 * the complete set of properties are always reflected in the storage file.
 *
 * The {@code Preferences} also provides a way to have JavaFX {@code Property} instances that are
 * synchronized to the properties. Basically whenever the value of the {@code Property} changes, the
 * new data is stored in the preferences file.
 *
 * New {@code Preferences} instances should only be created via the {@code PreferencesBuilder}.
 *
 * @author Christian Seifert
 */

public class Preferences {

    private List<PreferencesListener> preferencesListeners = null;
    private Map<String, String> values = null;
    private Map<String, StringProperty> properties = null;

    Preferences(Map<String, String> values) {
        this.setPreferencesListeners(new CopyOnWriteArrayList<>());
        this.setValues(values);
        this.setProperties(new HashMap<>());
    }

    public synchronized <T> ObjectProperty<T> getObjectProperty(String key, StringConverter<T> stringConverter) {
        FallbackStringConverter<T> fallbackStringConverter = new FallbackStringConverter<>(stringConverter);
        StringProperty stringProperty = this.getStringProperty(key);
        ObjectProperty<T> objectProperty = new SimpleObjectProperty<>(fallbackStringConverter.fromString(stringProperty.getValue()));
        stringProperty.addListener((o, oldStringValue, newStringValue) -> {
            if (!Objects.equals(oldStringValue, newStringValue)) {
                T newObjectValue = stringConverter.fromString(newStringValue);
                if (!Objects.equals(objectProperty.getValue(), newObjectValue)) {
                    objectProperty.setValue(newObjectValue);
                }
            }
        });
        objectProperty.addListener((o, oldObjectValue, newObjectValue) -> {
            if (!Objects.equals(oldObjectValue, newObjectValue)) {
                String newStringValue = stringConverter.toString(newObjectValue);
                if (!Objects.equals(stringProperty.getValue(), newStringValue)) {
                    stringProperty.setValue(newStringValue);
                }
            }
        });
        return objectProperty;
    }

    public synchronized StringProperty getStringProperty(String key) {
        StringProperty stringProperty = this.getProperties().get(key);
        if (stringProperty == null) {
            stringProperty = new SimpleStringProperty(this.getStringValue(key).orElse(null));
            stringProperty.addListener((o, oldValue, newValue) -> this.setStringValue(key, newValue));
            this.getProperties().put(key, stringProperty);
        }
        return stringProperty;
    }

    public synchronized Optional<String> getStringValue(String key) {
        String storedValue = this.getValues().get(key);
        return StringUtils.isEmpty(storedValue) ? Optional.empty() : Optional.of(storedValue);
    }

    public synchronized boolean setStringValue(String key, String newValue) {
        String oldValue = this.getValues().get(key);
        if (Objects.equals(oldValue, newValue)) {
            return false;
        } else {
            this.getValues().put(key, newValue);
            StringProperty stringProperty = this.getProperties().get(key);
            if (stringProperty != null) {
                stringProperty.setValue(newValue);
            }
            this.getPreferencesListeners().forEach(listener -> listener.onPropertyChanged(key, oldValue, newValue));
            return true;
        }
    }

    /**
     * Creates a snapshot of the values in this {@code Preferences} objects. Any change made to the
     * {@code Preferences} object after the snapshot has been created will not be reflected in the
     * result of this method. Also changes to the result map will not change the content of this
     * {@code Preferences} object.
     */
    public Map<String, String> toMap() {
        return new HashMap<>(this.getValues());
    }

    private static class FallbackStringConverter<T> extends StringConverter<T> {

        private static final Logger log = LoggerFactory.getLogger(FallbackStringConverter.class);

        private StringConverter<T> delegee = null;

        private FallbackStringConverter(StringConverter<T> delegee) {
            this.setDelegee(delegee);
        }

        @Override
        public String toString(T object) {
            try {
                return this.getDelegee().toString(object);
            } catch (Exception e) {
                log.debug("Cannot convert using toString from: {}", object, e);
                return null;
            }
        }

        @Override
        public T fromString(String string) {
            try {
                return this.getDelegee().fromString(string);
            } catch (Exception e) {
                log.debug("Cannot convert using fromString from: {}", string, e);
                return null;
            }
        }

        private StringConverter<T> getDelegee() {
            return this.delegee;
        }
        private void setDelegee(StringConverter<T> delegee) {
            this.delegee = delegee;
        }

    }

    boolean addPreferencesListener(PreferencesListener listener) {
        return this.getPreferencesListeners().add(listener);
    }
    private List<PreferencesListener> getPreferencesListeners() {
        return this.preferencesListeners;
    }
    private void setPreferencesListeners(List<PreferencesListener> preferencesListeners) {
        this.preferencesListeners = preferencesListeners;
    }

    private Map<String, String> getValues() {
        return this.values;
    }
    private void setValues(Map<String, String> values) {
        this.values = values;
    }

    private Map<String, StringProperty> getProperties() {
        return this.properties;
    }
    private void setProperties(Map<String, StringProperty> properties) {
        this.properties = properties;
    }

}
