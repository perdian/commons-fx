package de.perdian.commons.fx.properties.converters;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IdentiyStringConverterTest {

    @Test
    public void toStringValid() {
        Assertions.assertEquals("foo", new IdentityStringConverter().toString("foo"));
    }

    @Test
    public void toStringNull() {
        Assertions.assertEquals(null, new IdentityStringConverter().toString(null));
    }

    @Test
    public void fromStringValid() {
        Assertions.assertEquals("foo", new IdentityStringConverter().fromString("foo"));
    }

    @Test
    public void fromStringNull() {
        Assertions.assertEquals(null, new IdentityStringConverter().fromString(null));
    }

}
