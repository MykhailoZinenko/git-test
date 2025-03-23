package com.colonygenesis.ui;

import com.colonygenesis.core.Game;
import com.colonygenesis.core.GameState;
import com.colonygenesis.util.LoggerUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.logging.Logger;

/**
 * Pause menu screen that appears when the player pauses the game.
 * Provides options to resume, save, load, or exit the game.
 */
public class PauseMenuScreen extends BorderPane implements IScreenController {
    private static final Logger LOGGER = LoggerUtil.getLogger(PauseMenuScreen.class);

    private final Game game;

    /**
     * Constructs a new pause menu screen for the specified game.
     *
     * @param game The current game
     */
    public PauseMenuScreen(Game game) {
        this.game = game;
        LOGGER.info("Creating pause menu for game: " + game.getColonyName());
        initializeUI();
    }

    /**
     * Initializes the UI components for the pause menu screen.
     */
    private void initializeUI() {
        LOGGER.fine("Initializing PauseMenuScreen UI");

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

        resumeButton.setOnAction(e -> {
            LOGGER.info("Resuming game");
            ScreenManager.getInstance().activateScreen(GameState.GAMEPLAY);
        });

        saveButton.setOnAction(e -> {
            LOGGER.info("Saving game");
            String saveFile = game.saveGame();
            if (saveFile != null) {
                LOGGER.info("Game saved successfully to: " + saveFile);
            } else {
                LOGGER.severe("Failed to save game");
            }
        });

        loadButton.setOnAction(e -> {
            LOGGER.info("Loading saved game");
            ScreenManager.getInstance().activateScreen(GameState.LOAD_GAME);
        });

        settingsButton.setOnAction(e -> {
            LOGGER.info("Opening settings (not implemented)");
        });

        exitButton.setOnAction(e -> {
            LOGGER.info("Saving and exiting to main menu");
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

    /**
     * Creates a styled menu button.
     *
     * @param text The button text
     * @return A styled button
     */
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
    public void initialize() {
        LOGGER.fine("PauseMenuScreen initialized");
    }

    @Override
    public void onShow() {
        LOGGER.fine("PauseMenuScreen shown");
    }

    @Override
    public void onHide() {
        LOGGER.fine("PauseMenuScreen hidden");
    }

    @Override
    public void update() {}
}