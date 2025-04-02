package com.colonygenesis.ui;

import com.colonygenesis.core.Game;
import com.colonygenesis.core.GameState;
import com.colonygenesis.map.Tile;
import com.colonygenesis.ui.components.GameControlBar;
import com.colonygenesis.ui.components.ResourceBar;
import com.colonygenesis.ui.components.TileInfoPanel;
import com.colonygenesis.ui.components.TurnInfoBar;
import com.colonygenesis.ui.debug.DebugOverlay;
import com.colonygenesis.ui.events.BuildingEvents;
import com.colonygenesis.ui.events.ColonyEvents;
import com.colonygenesis.ui.events.EventBus;
import com.colonygenesis.ui.events.ResourceEvents;
import com.colonygenesis.ui.events.TileEvents;
import com.colonygenesis.ui.events.TurnEvents;
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

    private Tile selectedTile;

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
        subscribeToEvents();
    }

    /**
     * Subscribes to events for reactive updates.
     */
    private void subscribeToEvents() {
        // Subscribe to resource events
        eventBus.subscribe(ResourceEvents.ResourcesUpdatedEvent.class, this::handleResourcesUpdated);

        // Subscribe to turn events
        eventBus.subscribe(TurnEvents.TurnAdvancedEvent.class, this::handleTurnAdvanced);
        eventBus.subscribe(TurnEvents.PhaseChangedEvent.class, this::handlePhaseChanged);

        // Subscribe to building events
        eventBus.subscribe(BuildingEvents.BuildingPlacedEvent.class, this::handleBuildingPlaced);
        eventBus.subscribe(BuildingEvents.BuildingCompletedEvent.class, this::handleBuildingCompleted);
        eventBus.subscribe(BuildingEvents.BuildingActivatedEvent.class, this::handleBuildingActivated);
        eventBus.subscribe(BuildingEvents.BuildingDeactivatedEvent.class, this::handleBuildingDeactivated);

        // Subscribe to tile events
        eventBus.subscribe(TileEvents.TileSelectedEvent.class, this::handleTileSelected);
        eventBus.subscribe(TileEvents.ColonizeTileEvent.class, this::handleColonizeTile);
        eventBus.subscribe(TileEvents.TileUpdatedEvent.class, this::handleTileUpdated);
        eventBus.subscribe(TileEvents.RefreshMapEvent.class, this::handleRefreshMap);

        // Subscribe to colony events
        eventBus.subscribe(ColonyEvents.PopulationChangedEvent.class, this::handlePopulationChanged);
    }

    private void handleResourcesUpdated(ResourceEvents.ResourcesUpdatedEvent event) {
        Platform.runLater(() -> {
            resourceBar.update(event.getResources(), event.getProduction(), event.getCapacity());
        });
    }

    private void handleTurnAdvanced(TurnEvents.TurnAdvancedEvent event) {
        Platform.runLater(() -> {
            turnInfoBar.update(event.getTurnNumber(), game.getTurnManager().getCurrentPhase());
        });
    }

    private void handlePhaseChanged(TurnEvents.PhaseChangedEvent event) {
        Platform.runLater(() -> {
            turnInfoBar.update(event.getTurnNumber(), event.getPhase());
        });
    }

    private void handleBuildingPlaced(BuildingEvents.BuildingPlacedEvent event) {
        // The map view will update from the TileUpdatedEvent that follows
        Platform.runLater(() -> {
            DialogUtil.showMessageDialog(
                    "Building Placed",
                    "Started construction of " + event.getBuilding().getName() +
                            " at " + event.getTile().getX() + "," + event.getTile().getY());
        });
    }

    private void handleBuildingCompleted(BuildingEvents.BuildingCompletedEvent event) {
        Platform.runLater(() -> {
            DialogUtil.showMessageDialog(
                    "Building Completed",
                    event.getBuilding().getName() + " construction completed!");
        });
    }

    private void handleBuildingActivated(BuildingEvents.BuildingActivatedEvent event) {
        // The efficiency is available in the event if needed
        // The UI components will update automatically from their subscriptions
    }

    private void handleBuildingDeactivated(BuildingEvents.BuildingDeactivatedEvent event) {
        // The UI components will update automatically from their subscriptions
    }

    private void handleTileSelected(TileEvents.TileSelectedEvent event) {
        selectedTile = event.getTile();

        // The TileInfoPanel will update automatically from its subscription

        // Get colonization cost for the tile
        if (selectedTile != null) {
            tileInfoPanel.setColonizationCost(game.getPlanet().getColonizationCost(selectedTile.getX(), selectedTile.getY()));
        }
    }

    private void handleColonizeTile(TileEvents.ColonizeTileEvent event) {
        Tile tile = event.getTile();
        if (tile == null) return;

        Result<Boolean> result = game.getPlanet().colonizeTile(tile.getX(), tile.getY());
        Platform.runLater(() -> {
            if (result.isFailure()) {
                DialogUtil.showMessageDialog("Cannot Colonize", result.getErrorMessage());
            } else {
                // The map will update automatically from the TileUpdatedEvent
                DialogUtil.showMessageDialog("Tile Colonized", "Successfully colonized tile at " + tile.getX() + "," + tile.getY());
            }
        });
    }

    private void handleTileUpdated(TileEvents.TileUpdatedEvent event) {
        // The map view will handle this directly
        // The TileInfoPanel will handle this if it's the selected tile
    }

    private void handleRefreshMap(TileEvents.RefreshMapEvent event) {
        // Tell the map view to refresh completely
        Platform.runLater(() -> {
            mapView.renderGrid();
        });
    }

    private void handlePopulationChanged(ColonyEvents.PopulationChangedEvent event) {
        // The ResourceBar will handle this via its own subscription
        // This is handled automatically
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

        tileInfoPanel = new TileInfoPanel(game);
        tileInfoPanel.setMaxWidth(300);

        HBox contentBox = new HBox();
        HBox.setHgrow(mapView, Priority.ALWAYS);
        contentBox.getChildren().addAll(mapView, tileInfoPanel);

        setCenter(contentBox);

        gameControlBar = new GameControlBar(() -> {
            Result<com.colonygenesis.core.TurnPhase> result = game.getTurnManager().advancePhase();
            if (result.isFailure()) {
                DialogUtil.showMessageDialog("Cannot Advance Phase", result.getErrorMessage());
            }
        });
        setBottom(gameControlBar);

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

        // Set initial planet info
        String planetInfo = game.getPlanet().getName() + " (" + game.getPlanet().getType().getName() + ")";
        planetInfoLabel.setText(planetInfo);

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

        Platform.runLater(this::requestFocus);
    }

    @Override
    public void onHide() {
        LOGGER.fine("GameplayScreen hidden");
    }

    @Override
    public void update() {
        // No need for explicit update - reactive updates through events
    }

    /**
     * Gets the current game instance.
     *
     * @return The current game
     */
    public Game getGame() {
        return game;
    }

    /**
     * Cleans up resources when the screen is no longer needed.
     * Unsubscribes from events to prevent memory leaks.
     */
    public void dispose() {
        // Clean up debug overlay
        if (debugOverlay != null) {
            debugOverlay.dispose();
        }
    }
}