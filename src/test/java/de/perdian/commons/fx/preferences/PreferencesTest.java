package de.perdian.commons.fx.preferences;

import com.google.common.jimfs.Jimfs;
import de.perdian.commons.fx.properties.converters.DoubleStringConverter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.text.NumberFormat;

public class PreferencesTest {

    @Test
    public void shouldWritePreferencesIntoNewFileAndReadAgain() throws IOException {
        try (FileSystem fileSystem = Jimfs.newFileSystem()) {

            Path preferencesFile = fileSystem.getPath("preferences");
            Preferences originalPreferences = new Preferences(preferencesFile);
            StringProperty originalFooProperty = originalPreferences.getStringProperty("foo", "fooDefaultValue");

            originalFooProperty.setValue("fooNewValue");

            Preferences reloadedPreferences = new Preferences(preferencesFile);
            StringProperty reloadedFooProperty = reloadedPreferences.getStringProperty("foo");
            Assertions.assertEquals("fooNewValue", reloadedFooProperty.getValue());

        }
    }

    @Test
    public void shouldWritePreferencesIntoNewFileAndReadAgainWithObjectProperty() throws IOException {
        try (FileSystem fileSystem = Jimfs.newFileSystem()) {

            Path preferencesFile = fileSystem.getPath("preferences");
            Preferences originalPreferences = new Preferences(preferencesFile);
            ObjectProperty<Number> originalFooProperty = originalPreferences.getObjectProperty("foo", 42, new DoubleStringConverter(NumberFormat.getNumberInstance()));
            originalFooProperty.setValue(43d);

            Preferences reloadedPreferences = new Preferences(preferencesFile);
            ObjectProperty<Number> reloadedFooProperty = reloadedPreferences.getObjectProperty("foo", 42, new DoubleStringConverter(NumberFormat.getNumberInstance()));
            Assertions.assertEquals(43d, reloadedFooProperty.getValue());

        }
    }

}
