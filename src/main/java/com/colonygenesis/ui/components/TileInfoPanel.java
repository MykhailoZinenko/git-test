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
import com.colonygenesis.ui.events.NotificationEvents;
import com.colonygenesis.ui.events.TileEvents;
import com.colonygenesis.ui.styling.AppTheme;
import com.colonygenesis.util.LoggerUtil;
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
import java.util.logging.Logger;

/**
 * Panel that displays information about a selected tile.
 */
public class TileInfoPanel extends GamePanel {
    private static final Logger LOGGER = LoggerUtil.getLogger(TileInfoPanel.class);

    private final Game game;
    private final EventBus eventBus;

    private final VBox contentBox;

    private final Label titleLabel;
    private final Label positionLabel;
    private final Label descriptionLabel;
    private final Label statusLabel;
    private final VBox resourcesBox;

    private final Button colonizeButton;
    private final Button buildButton;

    private VBox buildingBox;
    private Label buildingStatusLabel;
    private ProgressBar constructionProgress;
    private Spinner<Integer> workerSpinner;
    private Button assignWorkersButton;
    private Label buildingInfoLabel;

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

        initializeBuildingUI();

        initializeEventSubscriptions();
    }

    /**
     * Subscribes to events for reactive updates.
     */
    private void initializeEventSubscriptions() {
        eventBus.subscribe(TileEvents.TileSelectedEvent.class, this::handleTileSelected);

        eventBus.subscribe(TileEvents.TileUpdatedEvent.class, this::handleTileUpdated);

        eventBus.subscribe(BuildingEvents.BuildingCompletedEvent.class, this::handleBuildingCompleted);
        eventBus.subscribe(BuildingEvents.BuildingActivatedEvent.class, this::handleBuildingActivated);
        eventBus.subscribe(BuildingEvents.BuildingDeactivatedEvent.class, this::handleBuildingDeactivated);
        eventBus.subscribe(BuildingEvents.WorkersAssignedEvent.class, this::handleWorkersAssigned);
        eventBus.subscribe(BuildingEvents.BuildingConstructionProgressEvent.class, this::handleConstructionProgress);
    }

    /**
     * Handles the tile selection event.
     */
    private void handleTileSelected(TileEvents.TileSelectedEvent event) {
        Platform.runLater(() -> setTile(event.getTile()));
    }

    /**
     * Handles the tile update event.
     */
    private void handleTileUpdated(TileEvents.TileUpdatedEvent event) {
        Platform.runLater(() -> {
            if (selectedTile != null && selectedTile.equals(event.getTile())) {
                setTile(event.getTile());
            }
        });
    }

    /**
     * Handles the building completed event.
     */
    private void handleBuildingCompleted(BuildingEvents.BuildingCompletedEvent event) {
        Platform.runLater(() -> {
            if (selectedTile != null && selectedTile.equals(event.getTile())) {
                updateBuildingInfo();
            }
        });
    }

    /**
     * Handles the building activated event.
     */
    private void handleBuildingActivated(BuildingEvents.BuildingActivatedEvent event) {
        Platform.runLater(() -> {
            if (selectedTile != null && selectedTile.equals(event.getTile())) {
                updateBuildingInfo();
            }
        });
    }

    /**
     * Handles the building deactivated event.
     */
    private void handleBuildingDeactivated(BuildingEvents.BuildingDeactivatedEvent event) {
        Platform.runLater(() -> {
            if (selectedTile != null && selectedTile.equals(event.getTile())) {
                updateBuildingInfo();
            }
        });
    }

    /**
     * Handles the workers assigned event.
     */
    private void handleWorkersAssigned(BuildingEvents.WorkersAssignedEvent event) {
        Platform.runLater(() -> {
            if (selectedTile != null && selectedTile.equals(event.getBuilding().getLocation())) {
                updateBuildingInfo();
            }
        });
    }

    /**
     * Handles the construction progress event.
     */
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
        buildingBox = new VBox(10);
        buildingBox.setPadding(new Insets(5, 0, 5, 0));
        buildingBox.setVisible(false);

        buildingStatusLabel = new Label("");
        buildingStatusLabel.getStyleClass().add(AppTheme.STYLE_LABEL);
        buildingStatusLabel.setWrapText(true);

        constructionProgress = new ProgressBar(0);
        constructionProgress.setPrefWidth(200);
        constructionProgress.getStyleClass().add("construction-progress");

        HBox workerBox = new HBox(10);
        workerBox.setAlignment(Pos.CENTER_LEFT);

        Label workerLabel = new Label("Workers:");
        workerLabel.getStyleClass().add(AppTheme.STYLE_LABEL);

        workerSpinner = new Spinner<>(0, 20, 0, 1);
        workerSpinner.setEditable(true);
        workerSpinner.setPrefWidth(70);

        assignWorkersButton = new Button("Assign");
        assignWorkersButton.getStyleClass().addAll(AppTheme.STYLE_BUTTON, AppTheme.STYLE_BUTTON_PRIMARY);
        assignWorkersButton.setOnAction(e -> assignWorkers());

        workerBox.getChildren().addAll(workerLabel, workerSpinner, assignWorkersButton);

        buildingInfoLabel = new Label("");
        buildingInfoLabel.getStyleClass().add(AppTheme.STYLE_LABEL);
        buildingInfoLabel.setWrapText(true);

        buildingBox.getChildren().addAll(
                buildingStatusLabel,
                constructionProgress,
                workerBox,
                buildingInfoLabel
        );

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
            int toAssign = desiredWorkers - currentWorkers;
            building.setResourceManager(game.getResourceManager());
            int assigned = building.assignWorkers(toAssign);

            if (assigned < toAssign) {
                buildingInfoLabel.setText("Not enough available workers!");
                buildingInfoLabel.setTextFill(Color.ORANGE);

                eventBus.publish(new NotificationEvents.BuildingNotificationEvent(
                        building.getName(),
                        "Not enough available workers",
                        NotificationEvents.NotificationType.WARNING
                ));
            } else {
                eventBus.publish(new NotificationEvents.BuildingNotificationEvent(
                        building.getName(),
                        "Assigned " + assigned + " workers",
                        NotificationEvents.NotificationType.SUCCESS
                ));
            }
        } else if (desiredWorkers < currentWorkers) {
            int toRemove = currentWorkers - desiredWorkers;
            int removed = building.removeWorkers(toRemove);

            eventBus.publish(new NotificationEvents.BuildingNotificationEvent(
                    building.getName(),
                    "Removed " + removed + " workers",
                    NotificationEvents.NotificationType.INFO
            ));
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

            workerSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                    0, building.getWorkersRequired(), building.getWorkersAssigned()
            ));
            workerSpinner.setVisible(true);
            assignWorkersButton.setVisible(true);

            if (building instanceof ProductionBuilding prod) {
                buildingInfoLabel.setText("Produces: " +
                        prod.getBaseOutputAmount() + " " +
                        prod.getPrimaryOutputType().getName() + " per turn\n" +
                        "Workers: " + prod.getWorkersAssigned() + "/" + prod.getWorkersRequired() + "\n" +
                        "Efficiency: " + efficiency + "%");
                buildingInfoLabel.setTextFill(Color.WHITE);
                buildingInfoLabel.setVisible(true);
            }
            else if (building instanceof HabitationBuilding hab) {
                buildingInfoLabel.setText("Population: " +
                        hab.getOccupied() + "/" + hab.getCapacity() + "\n" +
                        "Growth: " + hab.getPopulationGrowthRate() + " per turn\n" +
                        "Comfort: " + (int)(hab.getComfortLevel() * 100) + "%");
                buildingInfoLabel.setTextFill(Color.WHITE);
                buildingInfoLabel.setVisible(true);
            }

        } else {
            int progress = building.getConstructionProgress();
            constructionProgress.setProgress(progress / 100.0);
            constructionProgress.setVisible(true);

            buildingStatusLabel.setText(building.getName() + " (Constructing: " + progress + "%)");
            buildingStatusLabel.setTextFill(Color.ORANGE);

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
                buildButton.setVisible(false);
                updateBuildingInfo();
            } else {
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

            boolean canAfford = true;

            for (Map.Entry<ResourceType, Integer> entry : colonizationCost.entrySet()) {
                ResourceType type = entry.getKey();
                int cost = entry.getValue();
                int available = game.getResourceManager().getResource(type);

                Label costLabel = new Label(type.getName() + ": " + cost);

                if (cost > available) {
                    costLabel.setTextFill(Color.RED);
                    canAfford = false;
                } else {
                    costLabel.setTextFill(Color.LIGHTGREEN);
                }

                resourcesBox.getChildren().add(costLabel);
            }

            colonizeButton.setDisable(!canAfford);
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

        StackPane rootPane = ScreenManager.getInstance().getRootPane();
        rootPane.getChildren().add(overlay);

        overlay.show();
    }

    public void dispose() {
        LOGGER.fine("Disposing TileInfoPanel resources");

        eventBus.unsubscribeAll(this);

        selectedTile = null;
        colonizationCost = null;
    }
}