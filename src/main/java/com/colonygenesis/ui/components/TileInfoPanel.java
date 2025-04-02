package com.colonygenesis.ui.components;

import com.colonygenesis.building.AbstractBuilding;
import com.colonygenesis.building.HabitationBuilding;
import com.colonygenesis.building.ProductionBuilding;
import com.colonygenesis.core.Game;
import com.colonygenesis.map.Tile;
import com.colonygenesis.resource.ResourceType;
import com.colonygenesis.ui.ScreenManager;
import com.colonygenesis.ui.events.BuildingEvents;
import com.colonygenesis.ui.events.EventBus;
import com.colonygenesis.ui.events.TileEvents;
import com.colonygenesis.ui.styling.AppTheme;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Map;

/**
 * Panel that displays information about a selected tile.
 */
public class TileInfoPanel extends GamePanel {

    private final Game game;
    private final EventBus eventBus;

    // Content structure
    private final VBox contentBox;

    // Tile information components
    private final Label titleLabel;
    private final Label positionLabel;
    private final Label descriptionLabel;
    private final Label statusLabel;
    private final VBox resourcesBox;

    // Colonization and building controls
    private final Button colonizeButton;
    private final Button buildButton;

    // Building information and controls
    private VBox buildingBox;
    private Label buildingStatusLabel;
    private ProgressBar constructionProgress;
    private Spinner<Integer> workerSpinner;
    private Button assignWorkersButton;
    private Label buildingInfoLabel;

    // Data
    private Tile selectedTile;
    private Map<ResourceType, Integer> colonizationCost;

    /**
     * Constructs a new tile info panel.
     */
    public TileInfoPanel(Game game) {
        super("Tile Information");

        this.game = game;
        this.eventBus = EventBus.getInstance();

        setStyle("-fx-background-color: rgba(28, 35, 64, 0.95);");

        contentBox = super.getContentArea();
        contentBox.setSpacing(10);
        contentBox.setPadding(new Insets(10));

        titleLabel = new Label("No Tile Selected");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.WHITE);

        positionLabel = new Label("");
        positionLabel.setTextFill(Color.WHITE);

        descriptionLabel = new Label("");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setTextFill(Color.WHITE);

        statusLabel = new Label("");
        statusLabel.setWrapText(true);
        statusLabel.setTextFill(Color.WHITE);

        resourcesBox = new VBox(5);
        resourcesBox.setPadding(new Insets(5, 0, 5, 0));

        colonizeButton = new Button("Colonize Tile");
        colonizeButton.getStyleClass().addAll(AppTheme.STYLE_BUTTON, AppTheme.STYLE_BUTTON_SUCCESS);
        colonizeButton.setPrefWidth(200);
        colonizeButton.setVisible(false);
        colonizeButton.setOnAction(e -> {
            if (selectedTile != null) {
                eventBus.publish(new TileEvents.ColonizeTileEvent(selectedTile));
            }
        });

        buildButton = new Button("Build Structure");
        buildButton.getStyleClass().addAll(AppTheme.STYLE_BUTTON, AppTheme.STYLE_BUTTON_PRIMARY);
        buildButton.setPrefWidth(200);
        buildButton.setVisible(false);
        buildButton.setOnAction(e -> {
            if (selectedTile != null) {
                showBuildingSelectionDialog();
            }
        });

        contentBox.getChildren().addAll(
                titleLabel,
                positionLabel,
                new Separator(),
                descriptionLabel,
                new Separator(),
                statusLabel,
                resourcesBox,
                colonizeButton,
                buildButton
        );

        // Initialize the building UI components
        initializeBuildingUI();

        // Subscribe to events
        subscribeToEvents();
    }

    /**
     * Subscribes to relevant events for reactive updates.
     */
    private void subscribeToEvents() {
        // Subscribe to tile selection events
        eventBus.subscribe(TileEvents.TileSelectedEvent.class, this::handleTileSelected);

        // Subscribe to tile update events
        eventBus.subscribe(TileEvents.TileUpdatedEvent.class, this::handleTileUpdated);

        // Subscribe to building events
        eventBus.subscribe(BuildingEvents.BuildingCompletedEvent.class, this::handleBuildingCompleted);
        eventBus.subscribe(BuildingEvents.BuildingActivatedEvent.class, this::handleBuildingActivated);
        eventBus.subscribe(BuildingEvents.BuildingDeactivatedEvent.class, this::handleBuildingDeactivated);
        eventBus.subscribe(BuildingEvents.WorkersAssignedEvent.class, this::handleWorkersAssigned);
        eventBus.subscribe(BuildingEvents.BuildingConstructionProgressEvent.class, this::handleConstructionProgress);
    }

    private void handleTileSelected(TileEvents.TileSelectedEvent event) {
        Platform.runLater(() -> {
            setTile(event.getTile());
        });
    }

    private void handleTileUpdated(TileEvents.TileUpdatedEvent event) {
        Platform.runLater(() -> {
            if (selectedTile != null && selectedTile.equals(event.getTile())) {
                setTile(event.getTile());
            }
        });
    }

    private void handleBuildingCompleted(BuildingEvents.BuildingCompletedEvent event) {
        Platform.runLater(() -> {
            if (selectedTile != null && selectedTile.equals(event.getTile())) {
                updateBuildingInfo();
            }
        });
    }

    private void handleBuildingActivated(BuildingEvents.BuildingActivatedEvent event) {
        Platform.runLater(() -> {
            if (selectedTile != null && selectedTile.equals(event.getTile())) {
                updateBuildingInfo();
            }
        });
    }

    private void handleBuildingDeactivated(BuildingEvents.BuildingDeactivatedEvent event) {
        Platform.runLater(() -> {
            if (selectedTile != null && selectedTile.equals(event.getTile())) {
                updateBuildingInfo();
            }
        });
    }

    private void handleWorkersAssigned(BuildingEvents.WorkersAssignedEvent event) {
        Platform.runLater(() -> {
            if (selectedTile != null && selectedTile.equals(event.getBuilding().getLocation())) {
                updateBuildingInfo();
            }
        });
    }

    private void handleConstructionProgress(BuildingEvents.BuildingConstructionProgressEvent event) {
        Platform.runLater(() -> {
            if (selectedTile != null && selectedTile.equals(event.getBuilding().getLocation())) {
                updateBuildingInfo();
            }
        });
    }

    /**
     * Initializes the UI components for displaying building information.
     */
    private void initializeBuildingUI() {
        // Container for all building-related UI elements
        buildingBox = new VBox(10);
        buildingBox.setPadding(new Insets(5, 0, 5, 0));
        buildingBox.setVisible(false);

        // Building name and status
        buildingStatusLabel = new Label("");
        buildingStatusLabel.getStyleClass().add(AppTheme.STYLE_LABEL);
        buildingStatusLabel.setWrapText(true);

        // Construction progress bar
        constructionProgress = new ProgressBar(0);
        constructionProgress.setPrefWidth(200);
        constructionProgress.getStyleClass().add("construction-progress");

        // Worker assignment controls
        HBox workerBox = new HBox(10);
        workerBox.setAlignment(Pos.CENTER_LEFT);

        Label workerLabel = new Label("Workers:");
        workerLabel.getStyleClass().add(AppTheme.STYLE_LABEL);

        // Spinner for selecting worker count
        workerSpinner = new Spinner<>(0, 20, 0, 1);
        workerSpinner.setEditable(true);
        workerSpinner.setPrefWidth(70);

        assignWorkersButton = new Button("Assign");
        assignWorkersButton.getStyleClass().addAll(AppTheme.STYLE_BUTTON, AppTheme.STYLE_BUTTON_PRIMARY);
        assignWorkersButton.setOnAction(e -> assignWorkers());

        workerBox.getChildren().addAll(workerLabel, workerSpinner, assignWorkersButton);

        // Label for building-specific information
        buildingInfoLabel = new Label("");
        buildingInfoLabel.getStyleClass().add(AppTheme.STYLE_LABEL);
        buildingInfoLabel.setWrapText(true);

        // Add all components to the building box
        buildingBox.getChildren().addAll(
                buildingStatusLabel,
                constructionProgress,
                workerBox,
                buildingInfoLabel
        );

        // Add the building box to the main content box
        contentBox.getChildren().add(buildingBox);
    }

    /**
     * Assigns workers to the current building.
     */
    private void assignWorkers() {
        if (selectedTile == null || !selectedTile.hasBuilding()) {
            return;
        }

        AbstractBuilding building = selectedTile.getBuilding();
        int desiredWorkers = workerSpinner.getValue();
        int currentWorkers = building.getWorkersAssigned();

        if (desiredWorkers > currentWorkers) {
            // Assigning more workers
            int toAssign = desiredWorkers - currentWorkers;
            int assigned = game.getColonyManager().assignWorkers(building, toAssign);

            if (assigned < toAssign) {
                // Show warning about not enough workers
                buildingInfoLabel.setText("Not enough available workers!");
                buildingInfoLabel.setTextFill(Color.ORANGE);
            }
        } else if (desiredWorkers < currentWorkers) {
            // Removing workers
            int toRemove = currentWorkers - desiredWorkers;
            game.getColonyManager().removeWorkers(building, toRemove);
        }
    }

    /**
     * Updates the building information display.
     */
    private void updateBuildingInfo() {
        if (selectedTile == null || !selectedTile.hasBuilding()) {
            buildingBox.setVisible(false);
            return;
        }

        AbstractBuilding building = selectedTile.getBuilding();
        buildingBox.setVisible(true);

        if (building.isComplete()) {
            // Completed building
            constructionProgress.setVisible(false);

            int efficiency = building.calculateEfficiency();
            if (building.isActive()) {
                if (efficiency < 100 && building.getWorkersRequired() > 0) {
                    buildingStatusLabel.setText(building.getName() + " (Active - " + efficiency + "% efficiency)");
                    buildingStatusLabel.setTextFill(Color.YELLOW);
                } else {
                    buildingStatusLabel.setText(building.getName() + " (Active)");
                    buildingStatusLabel.setTextFill(Color.LIGHTGREEN);
                }
            } else {
                buildingStatusLabel.setText(building.getName() + " (Inactive)");
                buildingStatusLabel.setTextFill(Color.ORANGE);
            }

            // Configure worker spinner
            workerSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                    0, building.getWorkersRequired(), building.getWorkersAssigned()
            ));
            workerSpinner.setVisible(true);
            assignWorkersButton.setVisible(true);

            // Show specific info based on building type
            if (building instanceof ProductionBuilding) {
                ProductionBuilding prod = (ProductionBuilding) building;
                buildingInfoLabel.setText("Produces: " +
                        prod.getBaseOutputAmount() + " " +
                        prod.getPrimaryOutputType().getName() + " per turn\n" +
                        "Workers: " + prod.getWorkersAssigned() + "/" + prod.getWorkersRequired() + "\n" +
                        "Efficiency: " + efficiency + "%");
                buildingInfoLabel.setTextFill(Color.WHITE);
                buildingInfoLabel.setVisible(true);
            }
            else if (building instanceof HabitationBuilding) {
                HabitationBuilding hab = (HabitationBuilding) building;
                buildingInfoLabel.setText("Population: " +
                        hab.getOccupied() + "/" + hab.getCapacity() + "\n" +
                        "Growth: " + hab.getPopulationGrowthRate() + " per turn\n" +
                        "Comfort: " + (int)(hab.getComfortLevel() * 100) + "%");
                buildingInfoLabel.setTextFill(Color.WHITE);
                buildingInfoLabel.setVisible(true);
            }

        } else {
            // Building under construction
            int progress = building.getConstructionProgress();
            constructionProgress.setProgress(progress / 100.0);
            constructionProgress.setVisible(true);

            buildingStatusLabel.setText(building.getName() + " (Constructing: " + progress + "%)");
            buildingStatusLabel.setTextFill(Color.ORANGE);

            // Hide worker controls during construction
            workerSpinner.setVisible(false);
            assignWorkersButton.setVisible(false);
            buildingInfoLabel.setVisible(false);
        }
    }

    /**
     * Sets the colonization cost for the current tile.
     */
    public void setColonizationCost(Map<ResourceType, Integer> cost) {
        this.colonizationCost = cost;
        updateDisplay();
    }

    /**
     * Updates the display with information about the selected tile.
     */
    public void setTile(Tile tile) {
        this.selectedTile = tile;
        updateDisplay();
    }

    /**
     * Updates the display with current information.
     */
    private void updateDisplay() {
        if (selectedTile == null) {
            titleLabel.setText("No Tile Selected");
            positionLabel.setText("");
            descriptionLabel.setText("");
            statusLabel.setText("");
            resourcesBox.getChildren().clear();
            colonizeButton.setVisible(false);
            buildButton.setVisible(false);
            buildingBox.setVisible(false);
            return;
        }

        titleLabel.setText(selectedTile.getTerrainType().getName());
        positionLabel.setText("Position: " + selectedTile.getX() + ", " + selectedTile.getY());
        descriptionLabel.setText(selectedTile.getTerrainType().getDescription());

        if (selectedTile.isColonized()) {
            statusLabel.setText("Status: Colonized");
            colonizeButton.setVisible(false);

            if (selectedTile.hasBuilding()) {
                // There's a building on this tile
                buildButton.setVisible(false);
                updateBuildingInfo();
            } else {
                // Colonized but no building
                buildButton.setVisible(true);
                buildingBox.setVisible(false);
            }
        } else if (!selectedTile.isHabitable()) {
            statusLabel.setText("Status: Not Habitable");
            colonizeButton.setVisible(false);
            buildButton.setVisible(false);
            buildingBox.setVisible(false);
        } else {
            statusLabel.setText("Status: Not Colonized");
            colonizeButton.setVisible(true);
            buildButton.setVisible(false);
            buildingBox.setVisible(false);
        }

        resourcesBox.getChildren().clear();
        Label resourceTitle = new Label("Resource Modifiers:");
        resourceTitle.setFont(Font.font("System", FontWeight.BOLD, 12));
        resourceTitle.setTextFill(Color.WHITE);
        resourcesBox.getChildren().add(resourceTitle);

        for (ResourceType type : ResourceType.values()) {
            double modifier = selectedTile.getTerrainType().getResourceModifier(type);
            if (modifier != 1.0) {
                Label modifierLabel = new Label(type.getName() + ": " + String.format("%.1f", modifier) + "x");
                modifierLabel.setTextFill(Color.WHITE);
                resourcesBox.getChildren().add(modifierLabel);
            }
        }

        if (colonizationCost != null && !colonizationCost.isEmpty() && !selectedTile.isColonized()) {
            Separator separator = new Separator();
            separator.setStyle("-fx-background-color: rgba(75, 115, 153, 0.5);");

            Label costTitle = new Label("Colonization Cost:");
            costTitle.setFont(Font.font("System", FontWeight.BOLD, 12));
            costTitle.setTextFill(Color.WHITE);

            resourcesBox.getChildren().add(separator);
            resourcesBox.getChildren().add(costTitle);

            for (Map.Entry<ResourceType, Integer> entry : colonizationCost.entrySet()) {
                Label costLabel = new Label(entry.getKey().getName() + ": " + entry.getValue());
                costLabel.setTextFill(Color.WHITE);
                resourcesBox.getChildren().add(costLabel);
            }
        }
    }

    /**
     * Shows the building selection overlay.
     */
    private void showBuildingSelectionDialog() {
        if (selectedTile == null || !selectedTile.isColonized()) {
            return;
        }

        BuildingSelectionOverlay overlay = new BuildingSelectionOverlay(selectedTile);

        // Add overlay to the ScreenManager's root pane
        StackPane rootPane = ScreenManager.getInstance().getRootPane();
        rootPane.getChildren().add(overlay);

        // Show with animation
        overlay.show();
    }
}