package com.colonygenesis.ui;

import com.colonygenesis.core.GameState;
import com.colonygenesis.util.LoggerUtil;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
    private Scene scene;
    private IScreenController currentScreen;
    private final Map<GameState, IScreenController> screens = new HashMap<>();

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
        this.scene = scene;
    }

    /**
     * Registers a screen controller for a game state.
     *
     * @param gameState The game state to register for
     * @param controller The screen controller
     */
    public void registerScreen(GameState gameState, IScreenController controller) {
        LOGGER.info("Registering screen for state: " + gameState);
        screens.put(gameState, controller);
        controller.initialize();
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
        scene.setRoot(currentScreen.getRoot());
        currentScreen.onShow();
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
}