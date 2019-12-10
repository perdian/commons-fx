package de.perdian.commons.fx.properties.converters;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javafx.util.StringConverter;

public class ExceptionSwallowingStringConverterTest {

    @Test
    public void toStringValid() {
        StringConverter<String> actualConverter = Mockito.mock(StringConverter.class);
        Mockito.when(actualConverter.toString(Mockito.eq("foo"))).thenReturn("bar");
        Assertions.assertEquals("bar", new ExceptionSwallowingStringConverter<>(actualConverter).toString("foo"));
    }

    @Test
    public void toStringWithError() {
        StringConverter<String> actualConverter = Mockito.mock(StringConverter.class);
        Mockito.when(actualConverter.toString(Mockito.any())).thenThrow(new IllegalArgumentException("INVALID!"));
        Assertions.assertEquals(null, new ExceptionSwallowingStringConverter<>(actualConverter).toString("foo"));
    }

    @Test
    public void fromStringValid() {
        StringConverter<String> actualConverter = Mockito.mock(StringConverter.class);
        Mockito.when(actualConverter.fromString(Mockito.eq("foo"))).thenReturn("bar");
        Assertions.assertEquals("bar", new ExceptionSwallowingStringConverter<>(actualConverter).fromString("foo"));
    }

    @Test
    public void fromStringWithError() {
        StringConverter<String> actualConverter = Mockito.mock(StringConverter.class);
        Mockito.when(actualConverter.fromString(Mockito.any())).thenThrow(new IllegalArgumentException("INVALID!"));
        Assertions.assertEquals(null, new ExceptionSwallowingStringConverter<>(actualConverter).fromString("foo"));
    }

}
