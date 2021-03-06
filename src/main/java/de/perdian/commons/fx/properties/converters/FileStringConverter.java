package de.perdian.commons.fx.properties.converters;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import javafx.util.StringConverter;

public class FileStringConverter extends StringConverter<File> {

    @Override
    public String toString(File object) {
        return object == null ? null : object.getAbsolutePath();
    }

    @Override
    public File fromString(String string) {
        return StringUtils.isEmpty(string) ? null : new File(string);
    }

}
