package com.colonygenesis.ui;

import com.colonygenesis.core.GameState;
import com.colonygenesis.map.PlanetType;
import com.colonygenesis.ui.styling.AppTheme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class GameSetupScreen extends BorderPane implements IScreenController {

    private TextField colonyNameField;
    private ComboBox<PlanetType> planetTypeComboBox;
    private TextArea planetDescription;

    public GameSetupScreen() {
        initializeUI();
    }

    private void initializeUI() {
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
            System.out.println("Attempting to find image resource...");
            var url = getClass().getResource("/images/space_background.jpg");
            System.out.println("Image URL: " + url);

            if (url != null) {
                setStyle("-fx-background-image: url('" + url.toExternalForm() + "'); " +
                        "-fx-background-size: cover;");
                System.out.println("Image found and applied");
            } else {
                System.out.println("Image not found, using gradient background");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading background: " + e.getMessage());
        }
    }

    private void startNewGame() {
        System.out.println("Starting new game with colony: " + colonyNameField.getText() +
                ", planet type: " + planetTypeComboBox.getValue());

        // TODO: Initialize game with selected parameters
    }

    @Override
    public Parent getRoot() {
        return this;
    }

    @Override
    public void initialize() {}

    @Override
    public void onShow() {
        colonyNameField.setText("New Colony");
        planetTypeComboBox.setValue(PlanetType.TEMPERATE);
    }

    @Override
    public void onHide() {}

    @Override
    public void update() {}
}