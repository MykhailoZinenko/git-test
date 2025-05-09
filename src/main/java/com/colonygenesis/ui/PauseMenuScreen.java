package com.colonygenesis.ui;

import com.colonygenesis.core.Game;
import com.colonygenesis.core.GameState;
import com.colonygenesis.ui.styling.AppTheme;
import com.colonygenesis.util.DialogUtil;
import com.colonygenesis.util.LoggerUtil;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.logging.Logger;

/**
 * Pause menu screen that appears when the player pauses the game.
 * Provides options to resume, save, load, or exit the game.
 * Shows as an overlay on top of the game screen.
 */
public class PauseMenuScreen extends StackPane implements IScreenController {
    private static final Logger LOGGER = LoggerUtil.getLogger(PauseMenuScreen.class);

    private final ScreenManager screenManager;

    private Label infoLabel;

    /**
     * Constructs a new pause menu screen.
     */
    public PauseMenuScreen() {
        this.screenManager = ScreenManager.getInstance();

        setStyle("-fx-background-color: rgba(16, 20, 36, 0.75);");

        LOGGER.info("Creating pause menu");
        initializeUI();
    }

    /**
     * Initializes the UI components for the pause menu screen.
     */
    private void initializeUI() {
        LOGGER.fine("Initializing PauseMenuScreen UI");

        VBox menuContainer = new VBox(15);
        menuContainer.setAlignment(Pos.CENTER);
        menuContainer.getStyleClass().add(AppTheme.STYLE_MENU_CONTAINER);
        menuContainer.setMaxWidth(400);
        menuContainer.setMaxHeight(500);

        Label titleLabel = new Label("Game Paused");
        titleLabel.getStyleClass().add(AppTheme.STYLE_TITLE);

        infoLabel = new Label("");
        infoLabel.getStyleClass().add(AppTheme.STYLE_SUBTITLE);

        Button resumeButton = createMenuButton("Resume Game");
        Button saveButton = createMenuButton("Save Game");
        Button loadButton = createMenuButton("Load Game");
        Button settingsButton = createMenuButton("Settings");
        Button exitButton = createMenuButton("Save and Exit");
        exitButton.getStyleClass().add(AppTheme.STYLE_BUTTON_WARNING);

        resumeButton.setOnAction(e -> {
            LOGGER.info("Resuming game");
            screenManager.activateScreen(GameState.GAMEPLAY);
        });

        saveButton.setOnAction(e -> {
            LOGGER.info("Saving game");
            Game currentGame = screenManager.getCurrentGame();
            if (currentGame != null) {
                String saveFile = currentGame.saveGame();
                if (saveFile != null) {
                    LOGGER.info("Game saved successfully to: " + saveFile);
                    DialogUtil.showMessageDialog("Game Saved", "Game saved successfully to: " + saveFile);
                } else {
                    LOGGER.severe("Failed to save game");
                    DialogUtil.showMessageDialog("Save Failed", "Failed to save the game. Please try again.");
                }
            } else {
                LOGGER.severe("Cannot save: No current game");
                DialogUtil.showMessageDialog("Save Failed", "No active game to save.");
            }
        });

        loadButton.setOnAction(e -> {
            LOGGER.info("Loading saved game");
            screenManager.activateScreen(GameState.LOAD_GAME);
        });

        settingsButton.setOnAction(e -> {
            LOGGER.info("Opening settings (not implemented)");
            DialogUtil.showMessageDialog("Settings", "Settings menu not implemented yet.");
        });

        exitButton.setOnAction(e -> {
            LOGGER.info("Save and exit requested");

            Game currentGame = screenManager.getCurrentGame();
            if (currentGame != null) {
                DialogUtil.showConfirmDialog(
                        "Exit to Main Menu",
                        "Do you want to save your game before exiting to the main menu?",
                        () -> {
                            // Yes - Save and exit
                            currentGame.saveGame();
                            screenManager.activateScreen(GameState.MAIN_MENU);
                        },
                        () -> {
                            // No - Just exit without saving
                            screenManager.activateScreen(GameState.MAIN_MENU);
                        }
                );
            } else {
                // No game to save
                screenManager.activateScreen(GameState.MAIN_MENU);
            }
        });

        menuContainer.getChildren().addAll(
                titleLabel,
                infoLabel,
                resumeButton,
                saveButton,
                loadButton,
                settingsButton,
                exitButton
        );

        menuContainer.setTranslateY(-20);
        menuContainer.setOpacity(0);

        getChildren().add(menuContainer);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), menuContainer);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(200), menuContainer);
        slideIn.setFromY(-20);
        slideIn.setToY(0);

        menuContainer.setUserData((Runnable) () -> {
            Game currentGame = screenManager.getCurrentGame();
            if (currentGame != null) {
                String gameInfo = "Turn: " + currentGame.getTurnManager().getTurnNumber() +
                        " - Phase: " + currentGame.getTurnManager().getCurrentPhase().getName() +
                        "\nColony: " + currentGame.getColonyName();
                infoLabel.setText(gameInfo);

                LOGGER.info("Showing pause menu for " + currentGame.getColonyName() +
                        " on turn " + currentGame.getTurnManager().getTurnNumber());
            } else {
                infoLabel.setText("No active game");
                LOGGER.warning("No current game available for pause menu");
            }

            fadeIn.play();
            slideIn.play();
        });
    }

    /**
     * Creates a menu button with the app style.
     *
     * @param text The button text
     * @return A styled button
     */
    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().addAll(AppTheme.STYLE_BUTTON, AppTheme.STYLE_BUTTON_MENU);
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

        if (screenManager.getCurrentGame() != null) {
            infoLabel.setText("Turn: " + screenManager.getCurrentGame().getTurnManager().getTurnNumber() +
                    " - Phase: " + screenManager.getCurrentGame().getTurnManager().getCurrentPhase().getName() +
                    " - Colony: " + screenManager.getCurrentGame().getColonyName());
            LOGGER.info("Showing pause menu for " + screenManager.getCurrentGame().getColonyName() +
                    " on turn " + screenManager.getCurrentGame().getTurnManager().getTurnNumber());
        }

        for (javafx.scene.Node child : getChildren()) {
            if (child.getUserData() instanceof Runnable) {
                ((Runnable) child.getUserData()).run();
            }
        }
    }

    @Override
    public void onHide() {
        LOGGER.fine("PauseMenuScreen hidden");
    }

    @Override
    public void update() {}
}