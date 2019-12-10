package de.perdian.commons.fx.preferences;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.util.StringConverter;

public class PreferencesTest {

    @Test
    public void getObjectProperty() {
        Preferences preferences = new Preferences(new HashMap<>(Map.of("aKey", "42")));
        ObjectProperty<Integer> aProperty = preferences.getObjectProperty("aKey", new StringConverter<>() {
            @Override public String toString(Integer object) {
                return object == null ? null : object.toString();
            }
            @Override public Integer fromString(String string) {
                return StringUtils.isEmpty(string) ? null : Integer.valueOf(string);
            }
        });
        Assertions.assertEquals(Integer.valueOf(42), aProperty.getValue());
        preferences.setStringValue("aKey", "43");
        Assertions.assertEquals(Integer.valueOf(43), aProperty.getValue());
        aProperty.setValue(44);
        Assertions.assertEquals("44", preferences.getStringValue("aKey").get());
    }

    @Test
    public void getObjectPropertyWithInvalidValueInStringConverter() {
        Preferences preferences = new Preferences(new HashMap<>(Map.of("aKey", "INVALID_1")));
        ObjectProperty<Integer> aProperty = preferences.getObjectProperty("aKey", new StringConverter<>() {
            @Override public String toString(Integer object) {
                return object == null ? null : object.toString();
            }
            @Override public Integer fromString(String string) {
                return StringUtils.isEmpty(string) ? null : Integer.valueOf(string);
            }
        });
        Assertions.assertEquals(null, aProperty.getValue());
        preferences.setStringValue("aKey", "INVALID_2");
        Assertions.assertEquals(null, aProperty.getValue());
    }

    @Test
    public void getStringProperty() {
        PreferencesListener preferencesListener = Mockito.mock(PreferencesListener.class);
        Preferences preferences = new Preferences(new HashMap<>(Map.of("aKey", "aValue")));
        preferences.addPreferencesListener(preferencesListener);
        StringProperty aProperty = preferences.getStringProperty("aKey");
        Assertions.assertEquals("aValue", aProperty.getValue());
        preferences.setStringValue("aKey", "newValueFromPreferences");
        Assertions.assertEquals("newValueFromPreferences", aProperty.getValue());
        aProperty.setValue("newValueFromProperty");
        Assertions.assertEquals("newValueFromProperty", preferences.getStringValue("aKey").get());
        Mockito.verify(preferencesListener).onPropertyChanged(Mockito.eq("aKey"), Mockito.eq("aValue"), Mockito.eq("newValueFromPreferences"));
        Mockito.verify(preferencesListener).onPropertyChanged(Mockito.eq("aKey"), Mockito.eq("newValueFromPreferences"), Mockito.eq("newValueFromProperty"));
        Mockito.verifyNoMoreInteractions(preferencesListener);
    }

    @Test
    public void getStringValue() {
        Preferences preferences = new Preferences(new HashMap<>(Map.of("aKey", "aValue")));
        Assertions.assertEquals("aValue", preferences.getStringValue("aKey").get());
        Assertions.assertTrue(preferences.getStringValue("INVALID").isEmpty());
    }

    @Test
    public void setStringValue() {
        Map<String, String> properties = new HashMap<>(Map.of("aKey", "aValue"));
        Preferences preferences = new Preferences(properties);
        Assertions.assertTrue(preferences.getStringValue("bKey").isEmpty());
        Assertions.assertTrue(preferences.setStringValue("bKey", "bValue"));
        Assertions.assertEquals("bValue", properties.get("bKey"));
        Assertions.assertEquals("bValue", preferences.getStringValue("bKey").get());
    }

    @Test
    public void setStringValueWithListener() {
        PreferencesListener preferencesListener = Mockito.mock(PreferencesListener.class);
        Map<String, String> properties = new HashMap<>(Map.of("aKey", "aValue"));
        Preferences preferences = new Preferences(properties);
        preferences.addPreferencesListener(preferencesListener);
        Assertions.assertTrue(preferences.getStringValue("bKey").isEmpty());
        Assertions.assertTrue(preferences.setStringValue("bKey", "bValue"));
        Assertions.assertEquals("bValue", properties.get("bKey"));
        Assertions.assertEquals("bValue", preferences.getStringValue("bKey").get());
        Mockito.verify(preferencesListener).onPropertyChanged(Mockito.eq("bKey"), Mockito.eq(null), Mockito.eq("bValue"));
        Mockito.verifyNoMoreInteractions(preferencesListener);
    }

    @Test
    public void setStringValueWithListenerNotInvokedDueToSameValue() {
        PreferencesListener preferencesListener = Mockito.mock(PreferencesListener.class);
        Map<String, String> properties = new HashMap<>(Map.of("aKey", "aValue"));
        Preferences preferences = new Preferences(properties);
        preferences.addPreferencesListener(preferencesListener);
        Assertions.assertFalse(preferences.setStringValue("aKey", "aValue"));
        Mockito.verifyNoInteractions(preferencesListener);
    }

    @Test
    public void toMap() {
        Preferences preferences = new Preferences(new HashMap<>(Map.of("aKey", "aValue")));
        Map<String, String> map = preferences.toMap();
        Assertions.assertEquals("aValue", map.get("aKey"));
        map.put("aKey", "newValue");
        Assertions.assertEquals("aValue", preferences.getStringValue("aKey").get());
        preferences.setStringValue("aKey", "fooValue");
        Assertions.assertEquals("newValue", map.get("aKey"));
    }

}
