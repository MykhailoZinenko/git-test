package com.colonygenesis.ui;

import com.colonygenesis.core.Game;
import com.colonygenesis.core.GameState;
import com.colonygenesis.ui.components.GameControlBar;
import com.colonygenesis.ui.components.ResourceBar;
import com.colonygenesis.ui.components.TurnInfoBar;
import com.colonygenesis.ui.debug.DebugOverlay;
import com.colonygenesis.ui.styling.AppTheme;
import com.colonygenesis.util.LoggerUtil;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;

import java.util.logging.Logger;

/**
 * Main gameplay screen of the application.
 * Uses modular components to display the game interface.
 */
public class GameplayScreen extends BorderPane implements IScreenController {
    private static final Logger LOGGER = LoggerUtil.getLogger(GameplayScreen.class);

    private final Game game;
    private MapView mapView;
    private boolean hasShownInitially = false;

    private ResourceBar resourceBar;
    private TurnInfoBar turnInfoBar;
    private GameControlBar gameControlBar;

    private DebugOverlay debugOverlay;
    private final KeyCombination debugToggleKey = new KeyCodeCombination(KeyCode.F3);

    /**
     * Constructs a new gameplay screen for the specified game.
     *
     * @param game The game to display
     */
    public GameplayScreen(Game game) {
        this.game = game;
        LOGGER.info("Creating gameplay screen for colony: " + game.getColonyName());

        getStyleClass().add(AppTheme.STYLE_SCREEN);

        initializeUI();
        setupDebugOverlay();
        setupKeyboardShortcuts();
    }

    /**
     * Initializes the UI components for the gameplay screen.
     */
    private void initializeUI() {
        LOGGER.fine("Initializing GameplayScreen UI");

        HBox headerBox = createHeader();
        headerBox.getStyleClass().add(AppTheme.STYLE_HEADER);
        setTop(headerBox);

        mapView = new MapView();
        mapView.getStyleClass().add(AppTheme.STYLE_MAP_VIEW);
        setCenter(mapView);

        gameControlBar = new GameControlBar(() -> {
            game.getTurnManager().advancePhase();
            updateDisplay();
        });
        setBottom(gameControlBar);

        updateDisplay();

        Platform.runLater(() -> mapView.resetView());
    }

    /**
     * Sets up and configures the debug overlay.
     */
    private void setupDebugOverlay() {
        debugOverlay = new DebugOverlay(game);

        // Set initial state (off by default)
        debugOverlay.setActive(false);

        // Connect to MapView for rendering statistics
        mapView.setDebugOverlay(debugOverlay);

        // Position in the top-right corner
        StackPane.setAlignment(debugOverlay, Pos.TOP_RIGHT);
        StackPane.setMargin(debugOverlay, new Insets(10));

        // Add to a stack pane over the map view
        StackPane mapStack = new StackPane();
        mapStack.getChildren().addAll(mapView, debugOverlay);
        setCenter(mapStack);

        LOGGER.fine("Debug overlay configured");
    }

    /**
     * Sets up keyboard shortcuts.
     */
    private void setupKeyboardShortcuts() {
        Platform.runLater(this::requestFocus);
        addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (debugToggleKey.match(event)) {
                toggleDebugOverlay();
                event.consume();
            }
        });
    }

    /**
     * Toggles the debug overlay visibility.
     */
    private void toggleDebugOverlay() {
        boolean isActive = debugOverlay.toggleActive();
        LOGGER.info("Debug overlay " + (isActive ? "enabled" : "disabled"));
    }

    /**
     * Creates the header component with resources and menu buttons.
     *
     * @return The header HBox
     */
    private HBox createHeader() {
        resourceBar = new ResourceBar();

        turnInfoBar = new TurnInfoBar(this::showMenu);

        HBox headerBox = new HBox();

        headerBox.getChildren().addAll(resourceBar, turnInfoBar);

        HBox.setHgrow(resourceBar, Priority.ALWAYS);

        return headerBox;
    }

    /**
     * Shows the pause menu.
     */
    private void showMenu() {
        LOGGER.info("Opening game menu");
        ScreenManager screenManager = ScreenManager.getInstance();

        if (!screenManager.isScreenRegistered(GameState.PAUSE_MENU)) {
            PauseMenuScreen pauseMenu = new PauseMenuScreen(game);
            screenManager.registerScreen(GameState.PAUSE_MENU, pauseMenu);
        }

        screenManager.activateScreen(GameState.PAUSE_MENU);
    }

    /**
     * Updates the display with current game state.
     */
    private void updateDisplay() {
        turnInfoBar.update(
                game.getCurrentTurn(),
                game.getTurnManager().getCurrentPhase()
        );

        resourceBar.update(
                game.getResourceManager().getAllResources(),
                game.getResourceManager().getAllNetProduction(),
                game.getResourceManager().getAllCapacity()
        );

        if (debugOverlay != null && debugOverlay.isVisible()) {
            int buildingCount = 0; // TODO: Get from building manager
            int otherEntities = 0; // TODO: Get from entity manager
            debugOverlay.setEntityCounts(buildingCount, otherEntities);
        }
    }

    @Override
    public Parent getRoot() {
        return this;
    }

    @Override
    public void initialize() {
        LOGGER.fine("GameplayScreen initialized");
    }

    @Override
    public void onShow() {
        LOGGER.fine("GameplayScreen shown");
        if (game.getCurrentTurn() == 1 && !hasShownInitially) {
            Platform.runLater(() -> mapView.resetView());
            hasShownInitially = true;
        }
        updateDisplay();

        Platform.runLater(this::requestFocus);
    }

    @Override
    public void onHide() {
        LOGGER.fine("GameplayScreen hidden");
    }

    @Override
    public void update() {
        updateDisplay();
    }

    /**
     * Cleans up resources when the screen is no longer needed.
     */
    public void dispose() {
        if (debugOverlay != null) {
            debugOverlay.dispose();
        }
    }
}