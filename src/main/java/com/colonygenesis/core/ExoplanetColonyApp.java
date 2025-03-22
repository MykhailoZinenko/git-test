package com.colonygenesis.core;

import com.colonygenesis.ui.MainMenuScreen;
import com.colonygenesis.ui.styling.AppTheme;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ExoplanetColonyApp extends Application {
    private Stage primaryStage;
    private Scene scene;
    private Game game;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        game = new Game();

        MainMenuScreen mainMenu = new MainMenuScreen();

        scene = new Scene(mainMenu, 1280, 800);

        scene.getStylesheets().addAll(
                AppTheme.MAIN_STYLESHEET,
                AppTheme.BOOTSTRAP_STYLESHEET
        );

        primaryStage.setTitle("Exoplanet: Colony Genesis");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    public Game getGame() {
        return game;
    }

    public static void main(String[] args) {
        launch(args);
    }
}