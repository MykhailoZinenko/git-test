package com.colonygenesis.ui;

import com.colonygenesis.core.Game;
import com.colonygenesis.core.GameState;
import com.colonygenesis.map.PlanetType;
import com.colonygenesis.ui.styling.AppTheme;
import com.colonygenesis.util.LoggerUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.logging.Logger;

/**
 * Game setup screen that allows players to configure a new game.
 * Provides options for colony name and planet type selection.
 */
public class GameSetupScreen extends BorderPane implements IScreenController {
    private static final Logger LOGGER = LoggerUtil.getLogger(GameSetupScreen.class);

    private TextField colonyNameField;
    private ComboBox<PlanetType> planetTypeComboBox;
    private TextArea planetDescription;

    /**
     * Constructs a new game setup screen and initializes the UI components.
     */
    public GameSetupScreen() {
        getStyleClass().addAll(AppTheme.STYLE_MENU_SCREEN, AppTheme.STYLE_SETUP_SCREEN);
        initializeUI();
    }

    /**
     * Initializes the UI components for the game setup screen.
     */
    private void initializeUI() {
        LOGGER.fine("Initializing GameSetupScreen UI");

        VBox formContainer = new VBox(20);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.getStyleClass().add(AppTheme.STYLE_MENU_CONTAINER);
        formContainer.setMaxWidth(600);

        Label titleLabel = new Label("New Colony Setup");
        titleLabel.getStyleClass().add(AppTheme.STYLE_TITLE);

        GridPane setupGrid = new GridPane();
        setupGrid.setHgap(20);
        setupGrid.setVgap(15);
        setupGrid.setPadding(new Insets(20, 10, 20, 10));
        setupGrid.setAlignment(Pos.CENTER);

        Label nameLabel = new Label("Colony Name:");
        nameLabel.getStyleClass().add(AppTheme.STYLE_LABEL);

        colonyNameField = new TextField("New Colony");
        colonyNameField.getStyleClass().add(AppTheme.STYLE_TEXT_FIELD);
        colonyNameField.setPrefWidth(300);

        Label planetTypeLabel = new Label("Planet Type:");
        planetTypeLabel.getStyleClass().add(AppTheme.STYLE_LABEL);

        planetTypeComboBox = new ComboBox<>();
        planetTypeComboBox.getStyleClass().add(AppTheme.STYLE_COMBO_BOX);
        planetTypeComboBox.getItems().addAll(PlanetType.values());
        planetTypeComboBox.setValue(PlanetType.TEMPERATE);
        planetTypeComboBox.setPrefWidth(300);

        Label descriptionLabel = new Label("Description:");
        descriptionLabel.getStyleClass().add(AppTheme.STYLE_LABEL);

        planetDescription = new TextArea();
        planetDescription.setEditable(false);
        planetDescription.setWrapText(true);
        planetDescription.setPrefRowCount(4);
        planetDescription.setPrefWidth(300);
        planetDescription.getStyleClass().add(AppTheme.STYLE_TEXT_FIELD);

        planetTypeComboBox.setOnAction(e ->
                planetDescription.setText(planetTypeComboBox.getValue().getDescription())
        );

        planetDescription.setText(planetTypeComboBox.getValue().getDescription());

        setupGrid.add(nameLabel, 0, 0);
        setupGrid.add(colonyNameField, 1, 0);
        setupGrid.add(planetTypeLabel, 0, 1);
        setupGrid.add(planetTypeComboBox, 1, 1);
        setupGrid.add(descriptionLabel, 0, 2);
        setupGrid.add(planetDescription, 1, 2);

        Button backButton = new Button("Back");
        backButton.getStyleClass().addAll(AppTheme.STYLE_BUTTON, AppTheme.STYLE_BUTTON_PRIMARY);
        backButton.setOnAction(e -> ScreenManager.getInstance().activateScreen(GameState.MAIN_MENU));

        Button startButton = new Button("Start Game");
        startButton.getStyleClass().addAll(AppTheme.STYLE_BUTTON, AppTheme.STYLE_BUTTON_SUCCESS);
        startButton.setOnAction(e -> startNewGame());

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(backButton, startButton);

        formContainer.getChildren().addAll(titleLabel, setupGrid, buttonBox);

        setCenter(formContainer);
    }

    /**
     * Starts a new game with the configured settings and transitions to the gameplay screen.
     */
    private void startNewGame() {
        String colonyName = colonyNameField.getText();
        PlanetType planetType = planetTypeComboBox.getValue();

        LOGGER.info("Starting new game with colony: " + colonyName + ", planet type: " + planetType);

        Game game = new Game();
        game.initialize(colonyName, planetType, 30);
        game.start();

        GameplayScreen gameplayScreen = new GameplayScreen(game);
        ScreenManager.getInstance().registerScreen(GameState.GAMEPLAY, gameplayScreen);
        ScreenManager.getInstance().activateScreen(GameState.GAMEPLAY);
    }

    @Override
    public Parent getRoot() {
        return this;
    }

    @Override
    public void initialize() {
        LOGGER.fine("GameSetupScreen initialized");
    }

    @Override
    public void onShow() {
        LOGGER.fine("GameSetupScreen shown");
        colonyNameField.setText("New Colony");
        planetTypeComboBox.setValue(PlanetType.TEMPERATE);
    }

    @Override
    public void onHide() {
        LOGGER.fine("GameSetupScreen hidden");
    }

    @Override
    public void update() {}
}