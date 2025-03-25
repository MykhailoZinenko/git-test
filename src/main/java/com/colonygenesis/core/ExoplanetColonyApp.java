package com.colonygenesis.core;

import com.colonygenesis.ui.GameSetupScreen;
import com.colonygenesis.ui.MainMenuScreen;
import com.colonygenesis.ui.LoadGameScreen;
import com.colonygenesis.ui.ScreenManager;
import com.colonygenesis.ui.styling.AppTheme;
import com.colonygenesis.util.LoggerUtil;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.logging.Logger;

/**
 * Main application class for Exoplanet: Colony Genesis.
 * Initializes the game environment and UI components.
 */
public class ExoplanetColonyApp extends Application {
    private static final Logger LOGGER = LoggerUtil.getLogger(ExoplanetColonyApp.class);

    private Stage primaryStage;
    private Scene scene;
    private Game game;

    /**
     * JavaFX application entry point.
     * Sets up the primary stage, initializes the game and UI components.
     *
     * @param primaryStage The primary stage for this application
     */
    @Override
    public void start(Stage primaryStage) {
        LoggerUtil.initialize();
        LOGGER.info("Starting Exoplanet: Colony Genesis");

        this.primaryStage = primaryStage;

        StackPane root = new StackPane();
        scene = new Scene(root, 1280, 800);

        String cssPath = AppTheme.MAIN_STYLESHEET;
        URL cssURL = getClass().getResource("/" + cssPath);
        if (cssURL != null) {
            String externalForm = cssURL.toExternalForm();
            LOGGER.info("Loading CSS from: " + externalForm);
            scene.getStylesheets().add(externalForm);
        } else {
            LOGGER.warning("Could not find CSS file: " + cssPath);
        }

        scene.getStylesheets().add(AppTheme.BOOTSTRAP_STYLESHEET);

        primaryStage.setTitle("Exoplanet: Colony Genesis");
        primaryStage.setScene(scene);

        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.valueOf("F11"));
        primaryStage.setFullScreenExitHint("Press F11 to toggle fullscreen mode");

        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F11) {
                primaryStage.setFullScreen(!primaryStage.isFullScreen());
            }
            else if (event.getCode() == KeyCode.ESCAPE && primaryStage.isFullScreen()) {
                primaryStage.setFullScreen(false);
                event.consume();
            }
        });

        game = new Game();

        ScreenManager screenManager = ScreenManager.getInstance();
        screenManager.initialize(primaryStage, scene);

        screenManager.registerScreen(GameState.MAIN_MENU, new MainMenuScreen());
        screenManager.registerScreen(GameState.GAME_SETUP, new GameSetupScreen());
        screenManager.registerScreen(GameState.LOAD_GAME, new LoadGameScreen());

        screenManager.activateScreen(GameState.MAIN_MENU);

        LOGGER.info("Application initialized successfully");
        primaryStage.show();
    }

    /**
     * Gets the current game instance.
     *
     * @return The current game
     */
    public Game getGame() {
        return game;
    }

    /**
     * Application main entry point.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}