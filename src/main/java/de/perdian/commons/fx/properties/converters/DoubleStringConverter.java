package de.perdian.commons.fx.properties.converters;

import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
import java.text.ParseException;

public class DoubleStringConverter extends StringConverter<Number> {

    private static final Logger log = LoggerFactory.getLogger(DoubleStringConverter.class);

    private NumberFormat numberFormat = null;

    public DoubleStringConverter(NumberFormat numberFormat) {
        this.setNumberFormat(numberFormat);
    }

    @Override
    public String toString(Number object) {
        return object == null || object.doubleValue() == 0d ? "" : this.getNumberFormat().format(object);
    }

    @Override
    public Double fromString(String string) {
        if ("-".equalsIgnoreCase(string) || "+".equalsIgnoreCase(string)) {
            return Double.valueOf(0);
        } else if (string != null && !string.isEmpty()) {
            try {
                return this.getNumberFormat().parse(string).doubleValue();
            } catch (ParseException e) {
                log.trace("Invalid string value to convert into Double: {}", string, e);
            }
        }
        return null;
    }

    private NumberFormat getNumberFormat() {
        return this.numberFormat;
    }
    private void setNumberFormat(NumberFormat numberFormat) {
        this.numberFormat = numberFormat;
    }

}
