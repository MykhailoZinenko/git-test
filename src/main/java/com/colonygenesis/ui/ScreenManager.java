package com.colonygenesis.ui;

import com.colonygenesis.core.Game;
import com.colonygenesis.core.GameState;
import com.colonygenesis.util.LoggerUtil;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Manages screen navigation and transitions within the application.
 * Implements the Singleton pattern.
 */
public class ScreenManager {
    private static final Logger LOGGER = LoggerUtil.getLogger(ScreenManager.class);
    private static ScreenManager instance;

    private Stage primaryStage;
    private IScreenController currentScreen;
    private final Map<GameState, IScreenController> screens = new HashMap<>();
    private final StackPane rootPane = new StackPane();
    private Game currentGame;

    /**
     * Private constructor to prevent instantiation from outside.
     */
    private ScreenManager() {}

    /**
     * Gets the singleton instance of the ScreenManager.
     *
     * @return The ScreenManager instance
     */
    public static ScreenManager getInstance() {
        if (instance == null) {
            instance = new ScreenManager();
        }
        return instance;
    }

    /**
     * Initializes the ScreenManager with a stage and scene.
     *
     * @param primaryStage The primary stage
     * @param scene The main scene
     */
    public void initialize(Stage primaryStage, Scene scene) {
        LOGGER.info("Initializing ScreenManager with primary stage and scene");
        this.primaryStage = primaryStage;

        scene.setRoot(rootPane);
    }

    /**
     * Registers a screen controller for a game state.
     *
     * @param gameState The game state to register for
     * @param controller The screen controller
     */
    public void registerScreen(GameState gameState, IScreenController controller) {
        LOGGER.info("Registering screen for state: " + gameState);

        removeScreen(gameState);

        screens.put(gameState, controller);
        controller.initialize();
    }

    /**
     * Removes a screen and cleans up its resources.
     *
     * @param gameState The game state to remove
     */
    public void removeScreen(GameState gameState) {
        IScreenController screen = screens.get(gameState);
        if (screen != null) {
            LOGGER.info("Removing screen: " + gameState);

            if (screen instanceof GameplayScreen) {
                ((GameplayScreen) screen).dispose();
            }

            screens.remove(gameState);
        }
    }

    /**
     * Activates a screen for the specified game state.
     * Calls onHide() on the current screen and onShow() on the new screen.
     *
     * @param gameState The game state to activate
     * @throws IllegalArgumentException if no screen is registered for the state
     */
    public void activateScreen(GameState gameState) {
        LOGGER.info("Activating screen: " + gameState);
        IScreenController newScreen = screens.get(gameState);

        if (newScreen == null) {
            LOGGER.severe("No screen registered for state: " + gameState);
            throw new IllegalArgumentException("No screen registered for state: " + gameState);
        }

        if (currentScreen != null) {
            currentScreen.onHide();
        }

        currentScreen = newScreen;
        rootPane.getChildren().clear();
        rootPane.getChildren().add(currentScreen.getRoot());
        currentScreen.onShow();
    }

    /**
     * Shows a modal dialog with the specified content.
     * The dialog will be properly positioned over the fullscreen application.
     *
     * @param title The dialog title
     * @param content The dialog content node
     */
    public void showDialog(String title, javafx.scene.Node content) {
        Stage dialogStage = new Stage();
        dialogStage.initOwner(primaryStage);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.DECORATED);
        dialogStage.setTitle(title);

        Scene dialogScene = new Scene(new StackPane(content));
        dialogStage.setScene(dialogScene);

        dialogStage.setX(primaryStage.getX() + primaryStage.getWidth()/2 - dialogScene.getWidth()/2);
        dialogStage.setY(primaryStage.getY() + primaryStage.getHeight()/2 - dialogScene.getHeight()/2);

        dialogStage.showAndWait();
    }

    /**
     * Gets the current active screen controller.
     *
     * @return The current screen controller
     */
    public IScreenController getCurrentScreen() {
        return currentScreen;
    }

    /**
     * Gets the current game state.
     *
     * @return The current game state, or null if no screen is active
     */
    public GameState getCurrentGameState() {
        for (Map.Entry<GameState, IScreenController> entry : screens.entrySet()) {
            if (entry.getValue() == currentScreen) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Checks if a screen is registered for the specified game state.
     *
     * @param gameState The game state to check
     * @return true if a screen is registered, false otherwise
     */
    public boolean isScreenRegistered(GameState gameState) {
        return screens.containsKey(gameState);
    }

    /**
     * Gets the root pane that contains all screens.
     * Useful for adding overlays or popups.
     *
     * @return The root pane
     */
    public StackPane getRootPane() {
        return rootPane;
    }

    /**
     * Gets the primary stage.
     *
     * @return The primary stage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Gets the current game instance.
     *
     * @return The current game, or null if not in gameplay
     */
    public Game getGame() {
        if (getCurrentGameState() == GameState.GAMEPLAY && getCurrentScreen() instanceof GameplayScreen) {
            return ((GameplayScreen) getCurrentScreen()).getGame();
        }
        return null;
    }

    /**
     * Sets the current active game instance.
     * All screens that need access to game data should use this reference.
     *
     * @param game The current game instance
     */
    public void setCurrentGame(Game game) {
        LOGGER.info("Setting current game: " + (game != null ? game.getColonyName() : "null"));
        this.currentGame = game;
    }

    /**
     * Gets the current active game instance.
     *
     * @return The current game
     */
    public Game getCurrentGame() {
        return currentGame;
    }
}