package com.colonygenesis.ui;

import com.colonygenesis.core.Game;
import com.colonygenesis.core.GameState;
import com.colonygenesis.ui.components.GameControlBar;
import com.colonygenesis.ui.components.ResourceBar;
import com.colonygenesis.ui.components.TurnInfoBar;
import com.colonygenesis.ui.styling.AppTheme;
import com.colonygenesis.util.LoggerUtil;
import javafx.application.Platform;
import javafx.scene.Parent;
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
    private GameControlBar gameControlBar;

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
    }

    @Override
    public void onHide() {
        LOGGER.fine("GameplayScreen hidden");
    }

    @Override
    public void update() {
        updateDisplay();
    }
}