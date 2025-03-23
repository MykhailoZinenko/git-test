package com.colonygenesis.ui;

import com.colonygenesis.core.Game;
import com.colonygenesis.core.GameState;
import com.colonygenesis.ui.styling.AppTheme;
import com.colonygenesis.util.LoggerUtil;
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
import java.util.logging.Logger;

/**
 * Screen for loading saved games.
 * Displays a list of saved games and allows the player to select and load one.
 */
public class LoadGameScreen extends BorderPane implements IScreenController {
    private static final Logger LOGGER = LoggerUtil.getLogger(LoadGameScreen.class);

    private ListView<Game.SaveGameInfo> savesList;

    /**
     * Constructs a new load game screen and initializes the UI components.
     */
    public LoadGameScreen() {
        initializeUI();
    }

    /**
     * Initializes the UI components for the load game screen.
     */
    private void initializeUI() {
        LOGGER.fine("Initializing LoadGameScreen UI");

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
            if (newVal != null) {
                LOGGER.fine("Save game selected: " + newVal.colonyName());
            }
        });

        loadButton.setOnAction(e -> loadSelectedGame());
        backButton.setOnAction(e -> {
            LOGGER.info("Returning to main menu");
            ScreenManager.getInstance().activateScreen(GameState.MAIN_MENU);
        });

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(backButton, loadButton);

        container.getChildren().addAll(titleLabel, savesList, buttonBox);

        setCenter(container);
        setStyle("-fx-background-color: linear-gradient(to bottom, #0d1b2a, #1b263b, #415a77);");

        try {
            var url = getClass().getResource("/images/space_background.jpg");
            if (url != null) {
                setStyle("-fx-background-image: url('" + url.toExternalForm() + "'); " +
                        "-fx-background-size: cover;");
                LOGGER.fine("Background image applied successfully");
            } else {
                LOGGER.warning("Background image not found, using gradient background");
            }
        } catch (Exception e) {
            LOGGER.log(java.util.logging.Level.WARNING, "Error loading background image", e);
        }
    }

    /**
     * Loads the selected saved game and transitions to the gameplay screen.
     */
    private void loadSelectedGame() {
        Game.SaveGameInfo selected = savesList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            LOGGER.info("Loading game from: " + selected.filename());

            Game loadedGame = Game.loadGame(selected.filename());
            if (loadedGame != null) {
                GameplayScreen gameplayScreen = new GameplayScreen(loadedGame);
                ScreenManager.getInstance().registerScreen(GameState.GAMEPLAY, gameplayScreen);
                ScreenManager.getInstance().activateScreen(GameState.GAMEPLAY);
            } else {
                LOGGER.severe("Failed to load game from: " + selected.filename());
            }
        } else {
            LOGGER.warning("Attempted to load game with no selection");
        }
    }

    /**
     * Refreshes the list of saved games.
     */
    private void refreshSavesList() {
        LOGGER.fine("Refreshing saved games list");
        List<Game.SaveGameInfo> saves = Game.getSavedGames();
        savesList.getItems().clear();
        savesList.getItems().addAll(saves);
        LOGGER.info("Found " + saves.size() + " saved games");
    }

    @Override
    public Parent getRoot() {
        return this;
    }

    @Override
    public void initialize() {
        LOGGER.fine("LoadGameScreen initialized");
    }

    @Override
    public void onShow() {
        LOGGER.fine("LoadGameScreen shown");
        refreshSavesList();
    }

    @Override
    public void onHide() {
        LOGGER.fine("LoadGameScreen hidden");
    }

    @Override
    public void update() {}
}