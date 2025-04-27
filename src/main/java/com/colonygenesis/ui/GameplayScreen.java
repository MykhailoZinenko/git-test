package com.colonygenesis.ui;

import com.colonygenesis.core.Game;
import com.colonygenesis.core.GameState;
import com.colonygenesis.core.TurnPhase;
import com.colonygenesis.map.Tile;
import com.colonygenesis.ui.components.*;
import com.colonygenesis.ui.debug.DebugOverlay;
import com.colonygenesis.ui.events.*;
import com.colonygenesis.ui.styling.AppTheme;
import com.colonygenesis.util.DialogUtil;
import com.colonygenesis.util.LoggerUtil;
import com.colonygenesis.util.Result;
import com.colonygenesis.victory.VictoryEvents;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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

    private Game game;
    private MapView mapView;
    private boolean hasShownInitially = false;

    private ResourceBar resourceBar;
    private TurnInfoBar turnInfoBar;
    private TileInfoPanel tileInfoPanel;
    private NotificationManager notificationManager;

    private DebugOverlay debugOverlay;
    private final KeyCombination debugToggleKey = new KeyCodeCombination(KeyCode.F3);
    private final EventBus eventBus = EventBus.getInstance();

    private Tile selectedTile;
    private AlienCompoundPanel alienCompoundPanel;

    private HBox baseContent = new HBox();

    /**
     * Constructs a new gameplay screen for the specified game.
     *
     * @param game The game to display
     */
    public GameplayScreen(Game game) {
        this.game = game;
        LOGGER.info("Creating gameplay screen for colony: " + game.getColonyName() +
                ", Turn: " + game.getCurrentTurn());

        ScreenManager.getInstance().setCurrentGame(game);

        getStyleClass().add(AppTheme.STYLE_SCREEN);

        initializeUI();
        setupDebugOverlay();
        setupNotificationManager();
        setupKeyboardShortcuts();
        initializeEventSubscriptions();
    }

    /**
     * Initializes all event subscriptions for reactive updates.
     */
    private void initializeEventSubscriptions() {
        // Resource-related events
        eventBus.subscribe(ResourceEvents.ResourcesUpdatedEvent.class, this::handleResourcesUpdated);
        // Turn-related events
        eventBus.subscribe(TurnEvents.TurnAdvancedEvent.class, this::handleTurnAdvanced);
        eventBus.subscribe(TurnEvents.PhaseChangedEvent.class, this::handlePhaseChanged);

        // Building-related events
        eventBus.subscribe(BuildingEvents.BuildingPlacedEvent.class, this::handleBuildingPlaced);
        eventBus.subscribe(BuildingEvents.BuildingCompletedEvent.class, this::handleBuildingCompleted);
        eventBus.subscribe(BuildingEvents.BuildingActivatedEvent.class, this::handleBuildingActivated);
        eventBus.subscribe(BuildingEvents.BuildingDeactivatedEvent.class, this::handleBuildingDeactivated);
        eventBus.subscribe(BuildingEvents.BuildingConstructionProgressEvent.class, this::handleBuildingConstructionProgress);

        // Tile-related events
        eventBus.subscribe(TileEvents.TileSelectedEvent.class, this::handleTileSelected);
        eventBus.subscribe(TileEvents.ColonizeTileEvent.class, this::handleColonizeTile);
        eventBus.subscribe(TileEvents.TileUpdatedEvent.class, this::handleTileUpdated);

        // Colony-related events
        eventBus.subscribe(ColonyEvents.PopulationChangedEvent.class, this::handlePopulationChanged);
        eventBus.subscribe(ColonyEvents.WorkerAvailabilityChangedEvent.class, this::handleWorkerAvailabilityChanged);

        eventBus.subscribe(VictoryEvents.VictoryAchievedEvent.class, this::handleVictoryAchieved);
        eventBus.subscribe(VictoryEvents.GameOverEvent.class, this::handleGameOver);
    }

    /**
     * Handles updates to resources.
     */
    private void handleResourcesUpdated(ResourceEvents.ResourcesUpdatedEvent event) {
        Platform.runLater(() -> resourceBar.update(event.getResources(), event.getProduction(), event.getCapacity()));
    }

    /**
     * Handles turn advancement events.
     */
    private void handleTurnAdvanced(TurnEvents.TurnAdvancedEvent event) {
        Platform.runLater(() -> {
            turnInfoBar.update(event.getTurnNumber(), game.getTurnManager().getCurrentPhase());

            eventBus.publish(NotificationEvents.Factory.info(
                    "Turn " + event.getTurnNumber(),
                    "A new turn has begun."
            ));
        });
    }

    /**
     * Handles phase change events.
     */
    private void handlePhaseChanged(TurnEvents.PhaseChangedEvent event) {
        Platform.runLater(() -> turnInfoBar.update(event.getTurnNumber(), event.getPhase()));
    }

    /**
     * Handles building placement events.
     */
    private void handleBuildingPlaced(BuildingEvents.BuildingPlacedEvent event) {
        Platform.runLater(() -> eventBus.publish(new NotificationEvents.BuildingNotificationEvent(
                event.getBuilding().getName(),
                "Construction started",
                NotificationEvents.NotificationType.INFO
        )));
    }

    /**
     * Handles building completion events.
     */
    private void handleBuildingCompleted(BuildingEvents.BuildingCompletedEvent event) {
        Platform.runLater(() -> eventBus.publish(NotificationEvents.Factory.buildingCompleted(
                event.getBuilding().getName()
        )));
    }

    /**
     * Handles building activation events.
     */
    private void handleBuildingActivated(BuildingEvents.BuildingActivatedEvent event) {
        Platform.runLater(() -> {
            int efficiency = event.getEfficiency();
            if (efficiency < 100 && event.getBuilding().getWorkersRequired() > 0) {
                eventBus.publish(new NotificationEvents.BuildingNotificationEvent(
                        event.getBuilding().getName(),
                        "Operating at " + efficiency + "% efficiency",
                        NotificationEvents.NotificationType.INFO
                ));
            }
        });
    }

    /**
     * Handles building deactivation events.
     */
    private void handleBuildingDeactivated(BuildingEvents.BuildingDeactivatedEvent event) {
        Platform.runLater(() -> eventBus.publish(new NotificationEvents.BuildingNotificationEvent(
                event.getBuilding().getName(),
                "Building deactivated",
                NotificationEvents.NotificationType.WARNING
        )));
    }

    /**
     * Handles building construction progress events.
     */
    private void handleBuildingConstructionProgress(BuildingEvents.BuildingConstructionProgressEvent event) {
        if (event.getNewProgress() >= 75 && event.getPreviousProgress() < 75) {
            Platform.runLater(() -> eventBus.publish(new NotificationEvents.BuildingNotificationEvent(
                    event.getBuilding().getName(),
                    "Construction 75% complete",
                    NotificationEvents.NotificationType.INFO
            )));
        }
    }

    /**
     * Handles tile selection events.
     */
    private void handleTileSelected(TileEvents.TileSelectedEvent event) {
        selectedTile = event.getTile();

        if (selectedTile != null) {
            tileInfoPanel.setColonizationCost(game.getPlanet().getColonizationCost(
                    selectedTile.getX(), selectedTile.getY()
            ));
        }
    }

    /**
     * Handles tile colonization events.
     */
    private void handleColonizeTile(TileEvents.ColonizeTileEvent event) {
        Tile tile = event.getTile();
        if (tile == null) return;

        Result<Boolean> result = game.getPlanet().colonizeTile(tile.getX(), tile.getY());

        Platform.runLater(() -> {
            if (result.isFailure()) {
                eventBus.publish(NotificationEvents.Factory.error(
                        "Colonization Failed",
                        result.getErrorMessage()
                ));
            } else {
                eventBus.publish(NotificationEvents.Factory.success(
                        "Colonization Successful",
                        "Tile at " + tile.getX() + "," + tile.getY() + " colonized"
                ));
            }
        });
    }

    /**
     * Handles tile update events.
     */
    private void handleTileUpdated(TileEvents.TileUpdatedEvent event) {
        // The map view will handle this directly
        // The TileInfoPanel will handle this if it's the selected tile
    }

    /**
     * Handles population change events.
     */
    private void handlePopulationChanged(ColonyEvents.PopulationChangedEvent event) {
        Platform.runLater(() -> {
            int delta = event.getDelta();
            if (delta > 0) {
                eventBus.publish(NotificationEvents.Factory.info(
                        "Population Growth",
                        "Colony population increased by " + delta + " colonists"
                ));
            } else if (delta < 0) {
                eventBus.publish(NotificationEvents.Factory.warning(
                        "Population Decline",
                        "Colony population decreased by " + Math.abs(delta) + " colonists"
                ));
            }
        });
    }

    /**
     * Handles worker availability change events.
     */
    private void handleWorkerAvailabilityChanged(ColonyEvents.WorkerAvailabilityChangedEvent event) {
        Platform.runLater(() -> {
            int delta = event.getDelta();
            if (delta < 0 && Math.abs(delta) > 3) {
                eventBus.publish(NotificationEvents.Factory.warning(
                        "Worker Shortage",
                        "Available workers decreased by " + Math.abs(delta)
                ));
            } else if (delta > 5) {
                eventBus.publish(NotificationEvents.Factory.info(
                        "Workers Available",
                        delta + " additional workers are now available"
                ));
            }
        });
    }

    private void handleVictoryAchieved(VictoryEvents.VictoryAchievedEvent event) {
        Platform.runLater(() -> {
            // Create and show victory screen
            VictoryScreen victoryScreen = new VictoryScreen();
            victoryScreen.setVictoryType(event.getVictoryType());

            ScreenManager.getInstance().registerScreen(GameState.VICTORY, victoryScreen);
            ScreenManager.getInstance().activateScreen(GameState.VICTORY);
        });
    }

    private void handleGameOver(VictoryEvents.GameOverEvent event) {
        Platform.runLater(() -> {
            // Create and show game over screen
            GameOverScreen gameOverScreen = new GameOverScreen();
            gameOverScreen.setGameOverDetails(event.getReason(), event.getDescription());

            ScreenManager.getInstance().registerScreen(GameState.GAME_OVER, gameOverScreen);
            ScreenManager.getInstance().activateScreen(GameState.GAME_OVER);
        });
    }

    /**
     * Initializes the UI components for the gameplay screen.
     */
    private void initializeUI() {
        LOGGER.fine("Initializing GameplayScreen UI");

        // Header
        HBox headerBox = createHeader();
        headerBox.getStyleClass().add(AppTheme.STYLE_HEADER);
        setTop(headerBox);

        // Create all components first
        mapView = new MapView();
        mapView.setGrid(game.getPlanet().getGrid());
        mapView.getStyleClass().add(AppTheme.STYLE_MAP_VIEW);

        tileInfoPanel = new TileInfoPanel(game);
        tileInfoPanel.setMaxWidth(300);
        tileInfoPanel.setMinWidth(300);

        alienCompoundPanel = new AlienCompoundPanel(game);
        alienCompoundPanel.setMaxWidth(300);
        alienCompoundPanel.setMinWidth(250);

        // Create debug overlay
        debugOverlay = new DebugOverlay(game);
        debugOverlay.setActive(false);
        mapView.setDebugOverlay(debugOverlay);

        // Create notification manager
        notificationManager = new NotificationManager();

        // Build the layout from inside out

        // 1. Wrap mapView with debug overlay
        StackPane mapStack = new StackPane();
        mapStack.getChildren().addAll(mapView, debugOverlay);
        StackPane.setAlignment(debugOverlay, Pos.TOP_RIGHT);
        StackPane.setMargin(debugOverlay, new Insets(10));

        // 2. Create main content with all panels
        HBox contentBox = new HBox(10);
        HBox.setHgrow(mapStack, Priority.ALWAYS);  // Let map grow, not the panels
        contentBox.getChildren().addAll(alienCompoundPanel, mapStack, tileInfoPanel);

        // 3. Wrap everything with notification manager
        StackPane rootPane = new StackPane();
        rootPane.getChildren().addAll(contentBox, notificationManager);
        StackPane.setAlignment(notificationManager, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(notificationManager, new Insets(20));

        // Set the final layout
        setCenter(rootPane);

        // Footer
        GameControlBar gameControlBar = new GameControlBar(() -> {
            Result<TurnPhase> result = game.getTurnManager().advancePhase();
            if (result.isFailure()) {
                eventBus.publish(NotificationEvents.Factory.error(
                        "Phase Advancement Failed",
                        result.getErrorMessage()
                ));
            }
        }, () -> {
            ResearchOverlay overlay = new ResearchOverlay(game);
            StackPane screenRootPane = ScreenManager.getInstance().getRootPane();
            screenRootPane.getChildren().add(overlay);
            overlay.show();
        });

        setBottom(gameControlBar);

        // Setup keyboard shortcuts
        setupKeyboardShortcuts();

        Platform.runLater(() -> mapView.resetView());

        LOGGER.fine("GameplayScreen UI initialization complete");
    }

    // Remove or simplify these methods since we're handling everything in initializeUI
    private void setupDebugOverlay() {
        // This is now handled in initializeUI
        LOGGER.fine("Debug overlay already configured in initializeUI");
    }

    private void setupNotificationManager() {
        // This is now handled in initializeUI
        LOGGER.fine("Notification manager already configured in initializeUI");
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

        Label planetInfoLabel = new Label();
        planetInfoLabel.getStyleClass().add(AppTheme.STYLE_LABEL);
        HBox.setMargin(planetInfoLabel, new Insets(0, 20, 0, 10));

        turnInfoBar = new TurnInfoBar(this::showMenu);

        HBox headerBox = new HBox();
        headerBox.setSpacing(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        headerBox.getChildren().addAll(resourceBar, planetInfoLabel, turnInfoBar);

        HBox.setHgrow(resourceBar, Priority.ALWAYS);

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

        screenManager.setCurrentGame(game);

        if (!screenManager.isScreenRegistered(GameState.PAUSE_MENU)) {
            PauseMenuScreen pauseMenu = new PauseMenuScreen();
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

        if (game != null && resourceBar != null) {
            resourceBar.update(
                    game.getResourceManager().getAllResources(),
                    game.getResourceManager().getAllNetProduction(),
                    game.getResourceManager().getAllCapacity()
            );
        }

        assert game != null;
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
        LOGGER.info("Disposing GameplayScreen resources");

        eventBus.unsubscribeAll(this);

        // Dispose components
        if (mapView != null) {
            mapView.dispose();
        }

        if (tileInfoPanel != null) {
            tileInfoPanel.dispose();
        }

        if (resourceBar != null) {
            resourceBar.dispose();
        }

        if (notificationManager != null) {
            notificationManager.dispose();
        }

        if (debugOverlay != null) {
            debugOverlay.dispose();
        }

        // Clear references
        game = null;
        mapView = null;
        selectedTile = null;
    }
}