package com.colonygenesis.ui;

import com.colonygenesis.core.Game;
import com.colonygenesis.core.GameState;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class GameplayScreen extends BorderPane implements IScreenController {

    private final Game game;

    public GameplayScreen(Game game) {
        this.game = game;
        initializeUI();
    }

    private void initializeUI() {
        Label colonyInfoLabel = new Label(game.getColonyName() + " - " + game.getPlanetType());
        colonyInfoLabel.setFont(Font.font(24));
        colonyInfoLabel.setTextFill(javafx.scene.paint.Color.WHITE);

        Label turnLabel = new Label("Turn: " + game.getCurrentTurn());
        turnLabel.setFont(Font.font(18));
        turnLabel.setTextFill(javafx.scene.paint.Color.WHITE);

        VBox headerBox = new VBox(10);
        headerBox.setPadding(new Insets(20));
        headerBox.getChildren().addAll(colonyInfoLabel, turnLabel);

        Button saveButton = new Button("Save Game");
        saveButton.getStyleClass().add("primary-button");
        saveButton.setOnAction(e -> saveGame());

        Button menuButton = new Button("Exit to Menu");
        menuButton.getStyleClass().add("danger-button");
        menuButton.setOnAction(e -> ScreenManager.getInstance().activateScreen(GameState.MAIN_MENU));

        HBox controlBox = new HBox(10);
        controlBox.setPadding(new Insets(20));
        controlBox.getChildren().addAll(saveButton, menuButton);

        setTop(headerBox);
        setBottom(controlBox);

        setStyle("-fx-background-color: #121212;");
    }

    private void saveGame() {
        String saveFile = game.saveGame();
        if (saveFile != null) {
            System.out.println("Game saved successfully to: " + saveFile);
        } else {
            System.err.println("Failed to save game");
        }
    }

    @Override
    public Parent getRoot() {
        return this;
    }

    @Override
    public void initialize() {
    }

    @Override
    public void onShow() {
    }

    @Override
    public void onHide() {
    }

    @Override
    public void update() {
    }
}