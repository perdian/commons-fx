package de.perdian.commons.fx.properties.converters;

import javafx.util.StringConverter;

public class IdentityStringConverter extends StringConverter<String> {

    @Override
    public String toString(String object) {
        return object;
    }

    @Override
    public String fromString(String string) {
        return string;
    }

}
