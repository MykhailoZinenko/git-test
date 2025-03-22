package com.colonygenesis.ui;

import com.colonygenesis.core.GameState;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class ScreenManager {
    private static ScreenManager instance;

    private Stage primaryStage;
    private Scene scene;
    private IScreenController currentScreen;
    private final Map<GameState, IScreenController> screens = new HashMap<>();

    private ScreenManager() {}

    public static ScreenManager getInstance() {
        if (instance == null) {
            instance = new ScreenManager();
        }
        return instance;
    }

    public void initialize(Stage primaryStage, Scene scene) {
        this.primaryStage = primaryStage;
        this.scene = scene;
    }

    public void registerScreen(GameState gameState, IScreenController controller) {
        screens.put(gameState, controller);
        controller.initialize();
    }

    public void activateScreen(GameState gameState) {
        IScreenController newScreen = screens.get(gameState);

        if (newScreen == null) {
            throw new IllegalArgumentException("No screen registered for state: " + gameState);
        }

        if (currentScreen != null) {
            currentScreen.onHide();
        }

        currentScreen = newScreen;
        scene.setRoot(currentScreen.getRoot());
        currentScreen.onShow();
    }

    public IScreenController getCurrentScreen() {
        return currentScreen;
    }

    public GameState getCurrentGameState() {
        for (Map.Entry<GameState, IScreenController> entry : screens.entrySet()) {
            if (entry.getValue() == currentScreen) {
                return entry.getKey();
            }
        }
        return null;
    }
}