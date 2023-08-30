package de.perdian.commons.fx.properties.converters;

import javafx.util.StringConverter;

import java.io.File;

public class FileStringConverter extends StringConverter<File> {

    @Override
    public String toString(File object) {
        return object == null ? null : object.getAbsolutePath();
    }

    @Override
    public File fromString(String string) {
        return string == null || string.isEmpty() ? null : new File(string);
    }

}
