package de.perdian.commons.fx.properties.converters;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LocalDateStringConverterTest {

    @Test
    public void toStringValid() {
        Assertions.assertEquals("2000__01__02", new LocalDateStringConverter(DateTimeFormatter.ofPattern("yyyy__MM__dd"), null).toString(LocalDate.of(2000, 1, 2)));
    }

    @Test
    public void toStringNull() {
        Assertions.assertEquals(null, new LocalDateStringConverter(DateTimeFormatter.ofPattern("yyyy__MM__dd"), null).toString(null));
    }

    @Test
    public void fromStringValid() {
        Assertions.assertEquals(LocalDate.of(2000, 1, 2), new LocalDateStringConverter(null, List.of(DateTimeFormatter.ofPattern("yyyy__MM__dd"))).fromString("2000__01__02"));
        Assertions.assertEquals(LocalDate.of(2000, 1, 2), new LocalDateStringConverter(null, List.of(DateTimeFormatter.ofPattern("ddMMyyyy"), DateTimeFormatter.ofPattern("yyyy__MM__dd"))).fromString("2000__01__02"));
        Assertions.assertEquals(LocalDate.of(2000, 1, 2), new LocalDateStringConverter(null, List.of(DateTimeFormatter.ofPattern("ddMMyyyy"), DateTimeFormatter.ofPattern("yyyy__MM__dd"))).fromString("02012000"));
    }

    @Test
    public void fromStringInvalid() {
        Assertions.assertEquals(null, new LocalDateStringConverter(null, List.of(DateTimeFormatter.ofPattern("yyyy__MM__dd"))).fromString("INVALID"));
    }

    @Test
    public void fromStringNull() {
        Assertions.assertEquals(null, new LocalDateStringConverter(null, List.of(DateTimeFormatter.ofPattern("yyyy__MM__dd"))).fromString(null));
    }

}
