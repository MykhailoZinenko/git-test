package com.colonygenesis.core;

import com.colonygenesis.ui.GameSetupScreen;
import com.colonygenesis.ui.MainMenuScreen;
import com.colonygenesis.ui.LoadGameScreen;
import com.colonygenesis.ui.ScreenManager;
import com.colonygenesis.ui.styling.AppTheme;
import com.colonygenesis.util.LoggerUtil;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

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
        scene.getStylesheets().addAll(
                AppTheme.MAIN_STYLESHEET,
                AppTheme.BOOTSTRAP_STYLESHEET
        );

        primaryStage.setTitle("Exoplanet: Colony Genesis");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

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