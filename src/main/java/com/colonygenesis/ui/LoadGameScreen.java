package com.colonygenesis.ui;

import com.colonygenesis.core.Game;
import com.colonygenesis.core.GameState;
import com.colonygenesis.ui.styling.AppTheme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class LoadGameScreen extends BorderPane implements IScreenController {

    private ListView<Game.SaveGameInfo> savesList;

    public LoadGameScreen() {
        initializeUI();
    }

    private void initializeUI() {
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(30));
        container.setMaxWidth(800);
        container.setMaxHeight(600);
        container.setStyle("-fx-background-color: rgba(20, 20, 40, 0.85); -fx-background-radius: 10;");

        Label titleLabel = new Label("Load Game");
        titleLabel.getStyleClass().add(AppTheme.STYLE_TITLE);

        savesList = new ListView<>();
        savesList.setPrefHeight(400);
        savesList.setStyle("-fx-background-color: rgba(30, 30, 50, 0.9); -fx-text-fill: white;");

        Button loadButton = new Button("Load Selected Game");
        loadButton.getStyleClass().addAll(AppTheme.STYLE_MENU_BUTTON, "success-button");
        loadButton.setDisable(true);

        Button backButton = new Button("Back");
        backButton.getStyleClass().add(AppTheme.STYLE_MENU_BUTTON);

        savesList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            loadButton.setDisable(newVal == null);
        });

        loadButton.setOnAction(e -> loadSelectedGame());
        backButton.setOnAction(e -> ScreenManager.getInstance().activateScreen(GameState.MAIN_MENU));

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(backButton, loadButton);

        container.getChildren().addAll(titleLabel, savesList, buttonBox);

        setCenter(container);

        setStyle("-fx-background-color: linear-gradient(to bottom, #0d1b2a, #1b263b, #415a77);");

        try {
            System.out.println("Attempting to find image resource...");
            var url = getClass().getResource("/images/space_background.jpg");
            System.out.println("Image URL: " + url);

            if (url != null) {
                setStyle("-fx-background-image: url('" + url.toExternalForm() + "'); " +
                        "-fx-background-size: cover;");
                System.out.println("Image found and applied");
            } else {
                System.out.println("Image not found, using gradient background");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading background: " + e.getMessage());
        }
    }

    private void loadSelectedGame() {
        Game.SaveGameInfo selected = savesList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Game loadedGame = Game.loadGame(selected.filename());
            if (loadedGame != null) {
                GameplayScreen gameplayScreen = new GameplayScreen(loadedGame);
                ScreenManager.getInstance().registerScreen(GameState.GAMEPLAY, gameplayScreen);

                ScreenManager.getInstance().activateScreen(GameState.GAMEPLAY);
            }
        }
    }

    private void refreshSavesList() {
        List<Game.SaveGameInfo> saves = Game.getSavedGames();
        savesList.getItems().clear();
        savesList.getItems().addAll(saves);
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
        refreshSavesList();
    }

    @Override
    public void onHide() {
    }

    @Override
    public void update() {
    }
}