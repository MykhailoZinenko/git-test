package com.colonygenesis.ui;

import com.colonygenesis.core.Game;
import com.colonygenesis.core.GameState;
import com.colonygenesis.map.Tile;
import com.colonygenesis.ui.components.GameControlBar;
import com.colonygenesis.ui.components.ResourceBar;
import com.colonygenesis.ui.components.TileInfoPanel;
import com.colonygenesis.ui.components.TurnInfoBar;
import com.colonygenesis.ui.debug.DebugOverlay;
import com.colonygenesis.ui.events.EventBus;
import com.colonygenesis.ui.events.TileEvents;
import com.colonygenesis.ui.styling.AppTheme;
import com.colonygenesis.util.DialogUtil;
import com.colonygenesis.util.LoggerUtil;
import com.colonygenesis.util.Result;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
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
    private Label planetInfoLabel;
    private TileInfoPanel tileInfoPanel;

    private DebugOverlay debugOverlay;
    private final KeyCombination debugToggleKey = new KeyCodeCombination(KeyCode.F3);
    private final EventBus eventBus = EventBus.getInstance();

    /**
     * Constructs a new gameplay screen for the specified game.
     *
     * @param game The game to display
     */
    public GameplayScreen(Game game) {
        this.game = game;
        LOGGER.info("Creating gameplay screen for colony: " + game.getColonyName());

        getStyleClass().add(AppTheme.STYLE_SCREEN);

        eventBus.subscribe(TileEvents.TileSelectedEvent.class, this::handleTileSelected);
        eventBus.subscribe(TileEvents.ColonizeTileEvent.class, this::handleColonizeTile);

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
        mapView.setGrid(game.getPlanet().getGrid());
        mapView.getStyleClass().add(AppTheme.STYLE_MAP_VIEW);

        tileInfoPanel = new TileInfoPanel();
        tileInfoPanel.setMaxWidth(300);

        HBox contentBox = new HBox();
        HBox.setHgrow(mapView, Priority.ALWAYS);
        contentBox.getChildren().addAll(mapView, tileInfoPanel);

        setCenter(contentBox);

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

        debugOverlay.setActive(false);

        mapView.setDebugOverlay(debugOverlay);

        StackPane.setAlignment(debugOverlay, Pos.TOP_RIGHT);
        StackPane.setMargin(debugOverlay, new Insets(10));

        StackPane mapStack = new StackPane();
        mapStack.getChildren().add(mapView);
        mapStack.getChildren().add(debugOverlay);

        HBox contentBox = new HBox();
        HBox.setHgrow(mapStack, Priority.ALWAYS);
        contentBox.getChildren().addAll(mapStack, tileInfoPanel);

        setCenter(contentBox);

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
            } else if (event.getCode() == KeyCode.ESCAPE) {
                showMenu();
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

        planetInfoLabel = new Label();
        planetInfoLabel.getStyleClass().add(AppTheme.STYLE_LABEL);
        HBox.setMargin(planetInfoLabel, new Insets(0, 20, 0, 10));

        turnInfoBar = new TurnInfoBar(this::showMenu);

        HBox headerBox = new HBox();
        headerBox.setSpacing(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        headerBox.getChildren().addAll(resourceBar, planetInfoLabel, turnInfoBar);

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

        String planetInfo = game.getPlanet().getName() + " (" + game.getPlanet().getType().getName() + ")";
        planetInfoLabel.setText(planetInfo);

        if (debugOverlay != null && debugOverlay.isVisible()) {
            int buildingCount = 0; // TODO: Get from building manager
            int otherEntities = 0; // TODO: Get from entity manager
            debugOverlay.setEntityCounts(buildingCount, otherEntities);
        }

        eventBus.publish(new TileEvents.RefreshMapEvent());
    }

    /**
     * Handles tile selection events.
     * Updates the tile info panel with information about the selected tile.
     */
    private void handleTileSelected(TileEvents.TileSelectedEvent event) {
        Tile tile = event.getTile();
        if (tile != null) {
            tileInfoPanel.setColonizationCost(game.getPlanet().getColonizationCost(tile.getX(), tile.getY()));
        }
    }

    /**
     * Handles colonize tile events.
     * Attempts to colonize the specified tile and updates the UI accordingly.
     */
    private void handleColonizeTile(TileEvents.ColonizeTileEvent event) {
        Tile tile = event.getTile();
        if (tile == null) return;

        Result<Boolean> result = game.getPlanet().colonizeTile(tile.getX(), tile.getY());
        if (result.isFailure()) {
            DialogUtil.showMessageDialog("Cannot Colonize", result.getErrorMessage());
        } else {
            Tile updatedTile = game.getPlanet().getGrid().getTileAt(tile.getX(), tile.getY());

            eventBus.publish(new TileEvents.TileUpdatedEvent(updatedTile));

            updateDisplay();
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
        eventBus.clear();

        if (debugOverlay != null) {
            debugOverlay.dispose();
        }
    }
}