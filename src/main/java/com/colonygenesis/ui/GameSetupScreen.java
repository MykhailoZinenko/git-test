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
import javafx.scene.paint.Color;

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
        initializeUI();
    }

    /**
     * Initializes the UI components for the game setup screen.
     */
    private void initializeUI() {
        LOGGER.fine("Initializing GameSetupScreen UI");

        VBox formContainer = new VBox(20);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setPadding(new Insets(30));
        formContainer.setMaxWidth(600);
        formContainer.setBackground(new Background(new BackgroundFill(
                Color.rgb(20, 20, 40, 0.85),
                new CornerRadii(10),
                Insets.EMPTY
        )));

        Label titleLabel = new Label("New Colony Setup");
        titleLabel.getStyleClass().add(AppTheme.STYLE_TITLE);
        titleLabel.setTextFill(Color.WHITE);

        GridPane setupGrid = new GridPane();
        setupGrid.setHgap(20);
        setupGrid.setVgap(15);
        setupGrid.setPadding(new Insets(20, 10, 20, 10));
        setupGrid.setAlignment(Pos.CENTER);

        Label nameLabel = new Label("Colony Name:");
        nameLabel.setTextFill(Color.WHITE);
        nameLabel.setStyle("-fx-font-weight: bold;");

        colonyNameField = new TextField("New Colony");
        colonyNameField.setPrefWidth(300);

        Label planetTypeLabel = new Label("Planet Type:");
        planetTypeLabel.setTextFill(Color.WHITE);
        planetTypeLabel.setStyle("-fx-font-weight: bold;");

        planetTypeComboBox = new ComboBox<>();
        planetTypeComboBox.getItems().addAll(PlanetType.values());
        planetTypeComboBox.setValue(PlanetType.TEMPERATE);
        planetTypeComboBox.setPrefWidth(300);

        Label descriptionLabel = new Label("Description:");
        descriptionLabel.setTextFill(Color.WHITE);
        descriptionLabel.setStyle("-fx-font-weight: bold;");

        planetDescription = new TextArea();
        planetDescription.setEditable(false);
        planetDescription.setWrapText(true);
        planetDescription.setPrefRowCount(4);
        planetDescription.setPrefWidth(300);
        planetDescription.setStyle("-fx-control-inner-background: rgba(240, 240, 240, 0.9);");

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
        backButton.getStyleClass().add(AppTheme.STYLE_MENU_BUTTON);
        backButton.setOnAction(e -> ScreenManager.getInstance().activateScreen(GameState.MAIN_MENU));

        Button startButton = new Button("Start Game");
        startButton.getStyleClass().addAll(AppTheme.STYLE_MENU_BUTTON, "success-button");
        startButton.setOnAction(e -> startNewGame());

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(backButton, startButton);

        formContainer.getChildren().addAll(titleLabel, setupGrid, buttonBox);

        setCenter(formContainer);
        setStyle("-fx-background-color: linear-gradient(to bottom, #0d1b2a, #1b263b, #415a77);");

        try {
            var url = getClass().getResource("/images/space_background.jpg");
            if (url != null) {
                setStyle("-fx-background-image: url('" + url.toExternalForm() + "'); " +
                        "-fx-background-size: cover;");
                LOGGER.fine("Background image applied successfully");
            } else {
                LOGGER.warning("Background image not found, using gradient background");
            }
        } catch (Exception e) {
            LOGGER.log(java.util.logging.Level.WARNING, "Error loading background image", e);
        }
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