package com.colonygenesis.ui;

import com.colonygenesis.core.Game;
import com.colonygenesis.core.GameState;
import com.colonygenesis.ui.styling.AppTheme;
import com.colonygenesis.util.LoggerUtil;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

/**
 * Screen for loading saved games.
 * Displays a list of saved games and allows the player to select and load one.
 */
public class LoadGameScreen extends BorderPane implements IScreenController {
    private static final Logger LOGGER = LoggerUtil.getLogger(LoadGameScreen.class);

    private TableView<Game.SaveGameInfo> savesList;

    /**
     * Constructs a new load game screen and initializes the UI components.
     */
    public LoadGameScreen() {
        getStyleClass().addAll(AppTheme.STYLE_MENU_SCREEN, AppTheme.STYLE_LOAD_SCREEN);
        initializeUI();
    }

    private void initializeUI() {
        LOGGER.fine("Initializing LoadGameScreen UI");

        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.getStyleClass().add(AppTheme.STYLE_MENU_CONTAINER);
        container.setMaxWidth(800);
        container.setMaxHeight(600);

        Label titleLabel = new Label("Load Game");
        titleLabel.getStyleClass().add(AppTheme.STYLE_TITLE);

        TableView<Game.SaveGameInfo> savesTable = new TableView<>();
        savesTable.getStyleClass().add(AppTheme.STYLE_TABLE_VIEW);
        savesTable.setPrefHeight(400);

        TableColumn<Game.SaveGameInfo, String> colonyColumn = new TableColumn<>("Colony");
        colonyColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().colonyName()));
        colonyColumn.setPrefWidth(150);

        TableColumn<Game.SaveGameInfo, String> planetColumn = new TableColumn<>("Planet Type");
        planetColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().planetType().getName()));
        planetColumn.setPrefWidth(120);

        TableColumn<Game.SaveGameInfo, Integer> turnColumn = new TableColumn<>("Turn");
        turnColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().turn()).asObject());
        turnColumn.setPrefWidth(70);

        TableColumn<Game.SaveGameInfo, String> mapSizeColumn = new TableColumn<>("Map Size");
        mapSizeColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().mapSize() + "x" + data.getValue().mapSize()));
        mapSizeColumn.setPrefWidth(80);

        TableColumn<Game.SaveGameInfo, String> dateColumn = new TableColumn<>("Save Date");
        dateColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().saveDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        dateColumn.setPrefWidth(150);

        savesTable.getColumns().addAll(colonyColumn, planetColumn, turnColumn, mapSizeColumn, dateColumn);

        this.savesList = savesTable;

        Button loadButton = new Button("Load Selected Game");
        loadButton.getStyleClass().addAll(AppTheme.STYLE_BUTTON, AppTheme.STYLE_BUTTON_SUCCESS);
        loadButton.setDisable(true);

        Button backButton = new Button("Back");
        backButton.getStyleClass().addAll(AppTheme.STYLE_BUTTON, AppTheme.STYLE_BUTTON_PRIMARY);

        savesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            loadButton.setDisable(newVal == null);
            if (newVal != null) {
                LOGGER.fine("Save game selected: " + newVal.colonyName() + " Turn " + newVal.turn());
            }
        });

        loadButton.setOnAction(e -> loadSelectedGame());
        backButton.setOnAction(e -> {
            LOGGER.info("Returning to main menu");
            ScreenManager.getInstance().activateScreen(GameState.MAIN_MENU);
        });

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(backButton, loadButton);

        container.getChildren().addAll(titleLabel, savesTable, buttonBox);

        setCenter(container);
    }

    /**
     * Loads the selected saved game and transitions to the gameplay screen.
     */
    private void loadSelectedGame() {
        Game.SaveGameInfo selected = savesList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            LOGGER.info("Loading game from: " + selected.filename());

            Game.cleanup();

            Game loadedGame = Game.loadGame(selected.filename());
            if (loadedGame != null) {
                ScreenManager.getInstance().setCurrentGame(loadedGame);

                ScreenManager.getInstance().removeScreen(GameState.GAMEPLAY);

                GameplayScreen gameplayScreen = new GameplayScreen(loadedGame);
                ScreenManager.getInstance().registerScreen(GameState.GAMEPLAY, gameplayScreen);
                ScreenManager.getInstance().activateScreen(GameState.GAMEPLAY);
            } else {
                LOGGER.severe("Failed to load game from: " + selected.filename());
            }
        } else {
            LOGGER.warning("Attempted to load game with no selection");
        }
    }

    /**
     * Refreshes the list of saved games.
     */
    private void refreshSavesList() {
        LOGGER.fine("Refreshing saved games list");
        List<Game.SaveGameInfo> saves = Game.getSavedGames();
        savesList.getItems().clear();
        savesList.getItems().addAll(saves);
        LOGGER.info("Found " + saves.size() + " saved games");
    }

    @Override
    public Parent getRoot() {
        return this;
    }

    @Override
    public void initialize() {
        LOGGER.fine("LoadGameScreen initialized");
    }

    @Override
    public void onShow() {
        LOGGER.fine("LoadGameScreen shown");
        refreshSavesList();
    }

    @Override
    public void onHide() {
        LOGGER.fine("LoadGameScreen hidden");
    }

    @Override
    public void update() {}
}