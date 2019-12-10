package de.perdian.commons.fx.properties.converters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.util.StringConverter;

public class ExceptionSwallowingStringConverter<T> extends StringConverter<T> {

    private static final Logger log = LoggerFactory.getLogger(ExceptionSwallowingStringConverter.class);

    private StringConverter<T> delegee = null;

    public ExceptionSwallowingStringConverter(StringConverter<T> delegee) {
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