package de.perdian.commons.fx.properties.converters;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DoubleStringConverterTest {

    @Test
    public void toStringValid() {
        Assertions.assertEquals("1,23", new DoubleStringConverter(new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.GERMAN))).toString(Double.valueOf(1.23)));
    }

    @Test
    public void toStringNull() {
        Assertions.assertEquals("", new DoubleStringConverter(new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.GERMAN))).toString(null));
    }

    @Test
    public void fromStringValid() {
        Assertions.assertEquals(1.23d, new DoubleStringConverter(new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.GERMAN))).fromString("1,23"));
    }

    @Test
    public void fromStringInvalid() {
        Assertions.assertEquals(null, new DoubleStringConverter(new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.GERMAN))).fromString("INVALID"));
    }

    @Test
    public void fromStringNull() {
        Assertions.assertEquals(null, new DoubleStringConverter(new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.GERMAN))).fromString(null));
    }

}
