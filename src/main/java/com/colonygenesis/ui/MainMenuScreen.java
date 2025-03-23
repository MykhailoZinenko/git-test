package com.colonygenesis.ui;

import com.colonygenesis.core.GameState;
import com.colonygenesis.ui.styling.AppTheme;
import com.colonygenesis.util.LoggerUtil;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.logging.Logger;

/**
 * Main menu screen of the application.
 * Provides buttons for navigation to game setup, loading saved games, and other options.
 */
public class MainMenuScreen extends BorderPane implements IScreenController {
    private static final Logger LOGGER = LoggerUtil.getLogger(MainMenuScreen.class);

    /**
     * Constructs a new main menu screen and initializes the UI components.
     */
    public MainMenuScreen() {
        initializeUI();
    }

    /**
     * Initializes the UI components for the main menu.
     */
    private void initializeUI() {
        LOGGER.fine("Initializing MainMenuScreen UI");

        Label titleLabel = new Label("Exoplanet: Colony Genesis");
        titleLabel.getStyleClass().add(AppTheme.STYLE_TITLE);

        Button newGameBtn = createMenuButton("New Game");
        Button loadGameBtn = createMenuButton("Load Game");
        Button settingsBtn = createMenuButton("Settings");
        Button exitBtn = createMenuButton("Exit");
        exitBtn.getStyleClass().add("danger-button");

        newGameBtn.setOnAction(e -> handleNewGameRequest());
        loadGameBtn.setOnAction(e -> handleLoadGameRequest());
        settingsBtn.setOnAction(e -> handleSettingsRequest());
        exitBtn.setOnAction(e -> handleExitRequest());

        VBox menuBox = new VBox(20);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.getChildren().addAll(titleLabel, newGameBtn, loadGameBtn, settingsBtn, exitBtn);

        setCenter(menuBox);
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
     * Creates a styled menu button.
     *
     * @param text The button text
     * @return A styled button
     */
    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add(AppTheme.STYLE_MENU_BUTTON);
        return button;
    }

    /**
     * Handles the new game button click.
     */
    private void handleNewGameRequest() {
        LOGGER.info("New Game requested");
        ScreenManager.getInstance().activateScreen(GameState.GAME_SETUP);
    }

    /**
     * Handles the load game button click.
     */
    private void handleLoadGameRequest() {
        LOGGER.info("Load Game requested");
        ScreenManager.getInstance().activateScreen(GameState.LOAD_GAME);
    }

    /**
     * Handles the settings button click.
     */
    private void handleSettingsRequest() {
        LOGGER.info("Settings requested");
    }

    /**
     * Handles the exit button click.
     */
    private void handleExitRequest() {
        LOGGER.info("Exit requested - shutting down application");
        System.exit(0);
    }

    @Override
    public Parent getRoot() {
        return this;
    }

    @Override
    public void initialize() {
        LOGGER.fine("MainMenuScreen initialized");
    }

    @Override
    public void onShow() {
        LOGGER.fine("MainMenuScreen shown");
    }

    @Override
    public void onHide() {
        LOGGER.fine("MainMenuScreen hidden");
    }

    @Override
    public void update() {}
}