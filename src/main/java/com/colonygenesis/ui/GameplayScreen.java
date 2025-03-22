package com.colonygenesis.ui;

import com.colonygenesis.core.Game;
import com.colonygenesis.core.GameState;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class GameplayScreen extends BorderPane implements IScreenController {

    private Game game;
    private MapView mapView;
    private boolean hasShownInitially = false;

    public GameplayScreen(Game game) {
        this.game = game;
        initializeUI();
    }

    private void initializeUI() {
        Label colonyInfoLabel = new Label(game.getColonyName() + " - " + game.getPlanetType());
        colonyInfoLabel.setFont(Font.font(20));
        colonyInfoLabel.setTextFill(Color.WHITE);

        Label turnLabel = new Label("Turn: " + game.getCurrentTurn());
        turnLabel.setFont(Font.font(16));
        turnLabel.setTextFill(Color.WHITE);

        Button menuButton = new Button("Menu");
        menuButton.setStyle("-fx-background-color: #2E5077; -fx-text-fill: white;");
        menuButton.setOnAction(e -> showMenu());

        HBox headerBox = new HBox();
        headerBox.setPadding(new Insets(10));
        headerBox.setAlignment(Pos.CENTER_LEFT);

        HBox colonyInfoBox = new HBox(20);
        colonyInfoBox.getChildren().addAll(colonyInfoLabel, turnLabel);

        HBox menuButtonBox = new HBox();
        menuButtonBox.setAlignment(Pos.CENTER_RIGHT);
        menuButtonBox.getChildren().add(menuButton);
        HBox.setHgrow(menuButtonBox, Priority.ALWAYS);

        headerBox.getChildren().addAll(colonyInfoBox, menuButtonBox);

        mapView = new MapView();
        mapView.setPrefSize(800, 600);

        StackPane centerPanel = new StackPane();
        centerPanel.getChildren().add(mapView);

        setTop(headerBox);
        setCenter(centerPanel);

        setStyle("-fx-background-color: #121212;");

        Platform.runLater(() -> mapView.resetView());
    }

    private void showMenu() {
        ScreenManager screenManager = ScreenManager.getInstance();

        if (!screenManager.isScreenRegistered(GameState.PAUSE_MENU)) {
            PauseMenuScreen pauseMenu = new PauseMenuScreen(game);
            screenManager.registerScreen(GameState.PAUSE_MENU, pauseMenu);
        }

        screenManager.activateScreen(GameState.PAUSE_MENU);
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
        if (game.getCurrentTurn() == 1 && !hasShownInitially) {
            Platform.runLater(() -> mapView.resetView());
            hasShownInitially = true;
        }
    }

    @Override
    public void onHide() {
    }

    @Override
    public void update() {
    }
}