package de.perdian.commons.fx.properties.converters;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SimpleStringConverterTest {

    @Test
    public void toStringValid() {
        Assertions.assertEquals("S+foo", new SimpleStringConverter<>(v -> "S+" + v, s -> "V+" + s).toString("foo"));
    }

    @Test
    public void toStringNull() {
        Assertions.assertEquals("S+null", new SimpleStringConverter<>(v -> "S+" + v, s -> "V+" + s).toString(null));
    }

    @Test
    public void fromStringValid() {
        Assertions.assertEquals("V+foo", new SimpleStringConverter<>(v -> "S+" + v, s -> "V+" + s).fromString("foo"));
    }

    @Test
    public void fromStringNull() {
        Assertions.assertEquals("V+null", new SimpleStringConverter<>(v -> "S+" + v, s -> "V+" + s).fromString("null"));
    }

}
