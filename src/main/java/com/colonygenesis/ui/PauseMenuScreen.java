package com.colonygenesis.ui;

import com.colonygenesis.core.Game;
import com.colonygenesis.core.GameState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class PauseMenuScreen extends BorderPane implements IScreenController {

    private final Game game;

    public PauseMenuScreen(Game game) {
        this.game = game;
        initializeUI();
    }

    private void initializeUI() {
        setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        VBox menuContainer = new VBox(15);
        menuContainer.setAlignment(Pos.CENTER);
        menuContainer.setPadding(new Insets(30));
        menuContainer.setMaxWidth(400);
        menuContainer.setStyle("-fx-background-color: rgba(30, 40, 60, 0.9); " +
                "-fx-background-radius: 10;");

        Label titleLabel = new Label("Game Menu");
        titleLabel.setFont(javafx.scene.text.Font.font(24));
        titleLabel.setTextFill(Color.WHITE);

        Button resumeButton = createMenuButton("Resume Game");
        Button saveButton = createMenuButton("Save Game");
        Button loadButton = createMenuButton("Load Game");
        Button settingsButton = createMenuButton("Settings");
        Button exitButton = createMenuButton("Save and Exit");

        resumeButton.setOnAction(e -> ScreenManager.getInstance().activateScreen(GameState.GAMEPLAY));

        saveButton.setOnAction(e -> {
            String saveFile = game.saveGame();
            if (saveFile != null) {
                System.out.println("Game saved successfully to: " + saveFile);
            }
        });

        loadButton.setOnAction(e -> ScreenManager.getInstance().activateScreen(GameState.LOAD_GAME));

        settingsButton.setOnAction(e -> {});

        exitButton.setOnAction(e -> {
            game.saveGame();
            ScreenManager.getInstance().activateScreen(GameState.MAIN_MENU);
        });

        menuContainer.getChildren().addAll(
                titleLabel,
                resumeButton,
                saveButton,
                loadButton,
                settingsButton,
                exitButton
        );

        setCenter(menuContainer);
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(200);
        button.setPrefHeight(40);
        button.setStyle("-fx-background-color: #2E5077; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px;");
        return button;
    }

    @Override
    public Parent getRoot() {
        return this;
    }

    @Override
    public void initialize() {}

    @Override
    public void onShow() {}

    @Override
    public void onHide() {}

    @Override
    public void update() {}
}