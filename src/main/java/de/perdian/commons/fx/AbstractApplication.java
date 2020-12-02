package de.perdian.commons.fx;

import java.io.File;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.commons.fx.preferences.Preferences;
import de.perdian.commons.fx.preferences.PreferencesBuilder;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public abstract class AbstractApplication extends Application {

    private static final Logger log = LoggerFactory.getLogger(AbstractApplication.class);
    private Preferences preferences = null;

    @Override
    public void init() throws Exception {
        log.info("Creating preferences");
        this.setPreferences(this.createPreferences());
    }

    protected Preferences createPreferences() {
        return new PreferencesBuilder().path(this.resolveApplicationDirectory().resolve("preferences")).buildPreferences();
    }

    protected Path resolveApplicationDirectory() {
        return new File(System.getProperty("user.home"), "." + this.getClass().getSimpleName() + "/").toPath();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        log.info("Preparing JavaFX components");
        Pane mainPane = this.createMainPane();
        Scene mainScene = this.createMainScene(mainPane);

        log.info("Opening JavaFX stage");
        primaryStage.setScene(mainScene);
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        this.configurePrimaryStage(primaryStage);
        primaryStage.show();

    }

    protected void configurePrimaryStage(Stage primaryStage) {
    }

    protected abstract Pane createMainPane();

    protected Scene createMainScene(Pane mainPane) {
        return new Scene(mainPane);
    }

    public Preferences getPreferences() {
        return this.preferences;
    }
    private void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

}
