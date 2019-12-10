package de.perdian.commons.fx.properties;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.StringConverter;

public class PropertyFactoryTest {

    @Test
    public void createObjectProperty() {
        StringProperty stringProperty = new SimpleStringProperty("42");
        ObjectProperty<Integer> objectProperty = PropertyFactory.createObjectProperty(stringProperty, new StringConverter<>() {
            @Override public String toString(Integer object) {
                return object == null ? null : object.toString();
            }
            @Override public Integer fromString(String string) {
                return StringUtils.isEmpty(string) ? null : Integer.valueOf(string);
            }
        });
        Assertions.assertEquals(Integer.valueOf(42), objectProperty.getValue());
        stringProperty.setValue("43");
        Assertions.assertEquals(Integer.valueOf(43), objectProperty.getValue());
        objectProperty.setValue(44);
        Assertions.assertEquals("44", stringProperty.getValue());
    }

    @Test
    public void createObjectPropertyWithInvalidValueInStringConverter() {
        StringProperty stringProperty = new SimpleStringProperty();
        ObjectProperty<Integer> integerProperty = PropertyFactory.createObjectProperty(stringProperty, new StringConverter<>() {
            @Override public String toString(Integer object) {
                return object == null ? null : object.toString();
            }
            @Override public Integer fromString(String string) {
                return StringUtils.isEmpty(string) ? null : Integer.valueOf(string);
            }
        });
        Assertions.assertEquals(null, integerProperty.getValue());
        stringProperty.setValue("INVALID_2");
        Assertions.assertEquals(null, integerProperty.getValue());
    }

    @Test
    public void createIntegerProperty() {
        StringProperty stringProperty = new SimpleStringProperty("42");
        IntegerProperty integerProperty = PropertyFactory.createIntegerProperty(stringProperty, new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.GERMAN)));
        Assertions.assertEquals(Integer.valueOf(42), integerProperty.getValue());
        stringProperty.setValue("43,00");
        Assertions.assertEquals(Integer.valueOf(43), integerProperty.getValue());
        integerProperty.setValue(44);
        Assertions.assertEquals("44,00", stringProperty.getValue());
    }

    @Test
    public void createIntegerPropertyWithInvalidValueInStringConverter() {
        StringProperty stringProperty = new SimpleStringProperty();
        IntegerProperty integerProperty = PropertyFactory.createIntegerProperty(stringProperty, new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.GERMAN)));
        Assertions.assertEquals(Integer.valueOf(0), integerProperty.getValue());
        stringProperty.setValue("INVALID_2");
        Assertions.assertEquals(Integer.valueOf(0), integerProperty.getValue());
    }

}
