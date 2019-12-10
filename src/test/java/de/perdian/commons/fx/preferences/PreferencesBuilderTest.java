package de.perdian.commons.fx.preferences;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.common.jimfs.Jimfs;

public class PreferencesBuilderTest {

    @Test
    public void buildWithoutPath() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new PreferencesBuilder().buildPreferences();
        });
    }

    @Test
    public void build() throws IOException {
        try (FileSystem fileSystem = Jimfs.newFileSystem()) {
            Path fileSystemPath = fileSystem.getPath("a/b/test");
            Preferences originalPreferences = new PreferencesBuilder().path(fileSystemPath).buildPreferences();
            originalPreferences.setStringValue("a", "aValue");
            Preferences reloadedPreferences = new PreferencesBuilder().path(fileSystemPath).buildPreferences();
            Assertions.assertEquals("aValue", reloadedPreferences.getStringProperty("a").get());
        }
    }

}
