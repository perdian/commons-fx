package de.perdian.commons.fx.properties;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.StringConverter;

public class PropertyFactory {

    public static <T> ObjectProperty<T> createObjectProperty(Property<String> stringProperty, StringConverter<T> stringConverter) {
        ObjectProperty<T> objectProperty = new SimpleObjectProperty<>(stringConverter.fromString(stringProperty.getValue()));
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

    public static IntegerProperty createIntegerProperty(Property<String> stringProperty, NumberFormat numberFormat) {
        return PropertyFactory.initializeNumberProperty(new SimpleIntegerProperty(), stringProperty, numberFormat);
    }

    public static <T extends Property<Number>> T initializeNumberProperty(T numberProperty, Property<String> stringProperty, NumberFormat numberFormat) {
        if (StringUtils.isNotEmpty(stringProperty.getValue())) {
            try {
                numberProperty.setValue(numberFormat.parse(stringProperty.getValue()));
            } catch (ParseException e) {
                throw new IllegalArgumentException("Invalid number: " + stringProperty.getValue(), e);
            }
        }
        stringProperty.addListener((o, oldStringValue, newStringValue) -> {
            if (!Objects.equals(oldStringValue, newStringValue)) {
                try {
                    Number newNumberValue = StringUtils.isEmpty(newStringValue) ? null : numberFormat.parse(newStringValue);
                    if (!Objects.equals(numberProperty.getValue(), newNumberValue)) {
                        numberProperty.setValue(newNumberValue);
                    }
                } catch (ParseException e) {
                    throw new IllegalArgumentException("Invalid number: " + newStringValue, e);
                }
            }
        });
        numberProperty.addListener((o, oldNumberValue, newNumberValue) -> {
            if (!Objects.equals(oldNumberValue, newNumberValue)) {
                String newStringValue = numberFormat.format(newNumberValue);
                if (!Objects.equals(stringProperty.getValue(), newStringValue)) {
                    stringProperty.setValue(newStringValue);
                }
            }
        });
        return numberProperty;
    }

}
