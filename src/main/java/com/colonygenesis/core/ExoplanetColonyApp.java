package com.colonygenesis.core;

import com.colonygenesis.ui.GameSetupScreen;
import com.colonygenesis.ui.MainMenuScreen;
import com.colonygenesis.ui.LoadGameScreen;
import com.colonygenesis.ui.ScreenManager;
import com.colonygenesis.ui.styling.AppTheme;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ExoplanetColonyApp extends Application {
    private Stage primaryStage;
    private Scene scene;
    private Game game;

    @Override
    public void start(Stage primaryStage) {
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

        primaryStage.show();
    }

    public Game getGame() {
        return game;
    }

    public static void main(String[] args) {
        launch(args);
    }
}