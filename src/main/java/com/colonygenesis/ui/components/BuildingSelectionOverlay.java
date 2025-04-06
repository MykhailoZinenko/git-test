package com.colonygenesis.ui.components;

import com.colonygenesis.building.*;
import com.colonygenesis.core.Game;
import com.colonygenesis.core.GameState;
import com.colonygenesis.map.Tile;
import com.colonygenesis.resource.ResourceType;
import com.colonygenesis.ui.ScreenManager;
import com.colonygenesis.ui.events.EventBus;
import com.colonygenesis.ui.events.GameEvent;
import com.colonygenesis.ui.events.TileEvents;
import com.colonygenesis.ui.styling.AppTheme;
import com.colonygenesis.util.LoggerUtil;
import com.colonygenesis.util.Result;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Overlay panel for selecting and constructing buildings.
 */
public class BuildingSelectionOverlay extends StackPane {
    private static final Logger LOGGER = LoggerUtil.getLogger(BuildingSelectionOverlay.class);

    private final Game game;
    private final Tile tile;
    private final TabPane tabPane;
    private final VBox detailsBox;
    private final VBox contentContainer;
    private final Button buildButton;

    private AbstractBuilding selectedBuilding;
    private final EventBus eventBus = EventBus.getInstance();

    /**
     * Creates a new building selection overlay.
     *
     * @param tile The tile where the building will be constructed
     */
    public BuildingSelectionOverlay(Tile tile) {
        this.game = ScreenManager.getInstance().getGame();
        this.tile = tile;

        setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        setAlignment(Pos.CENTER);

        contentContainer = new VBox(20);
        contentContainer.setAlignment(Pos.CENTER);
        contentContainer.setMaxWidth(1000);
        contentContainer.setMaxHeight(800);
        contentContainer.getStyleClass().add(AppTheme.STYLE_MENU_CONTAINER);
        contentContainer.setStyle("-fx-background-color: rgba(16, 20, 36, 0.95);");
        contentContainer.setPadding(new Insets(20));

        Label titleLabel = new Label("Select Building to Construct");
        titleLabel.getStyleClass().add(AppTheme.STYLE_TITLE);

        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setPrefHeight(400);
        tabPane.getStyleClass().add(AppTheme.STYLE_PANEL);

        createProductionBuildingsTab();
        createHabitationBuildingsTab();

        detailsBox = new VBox(10);
        detailsBox.setPadding(new Insets(15));
        detailsBox.setAlignment(Pos.CENTER_LEFT);
        detailsBox.setMinHeight(150);
        detailsBox.getStyleClass().add("details-panel");

        Label noSelectionLabel = new Label("Select a building to view details");
        noSelectionLabel.getStyleClass().add("details-empty-msg");
        detailsBox.getChildren().add(noSelectionLabel);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getStyleClass().add("action-button-container");

        buildButton = new Button("Build");
        buildButton.getStyleClass().add("build-button");
        buildButton.setDisable(true);

        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("cancel-button");

        buttonBox.getChildren().addAll(cancelButton, buildButton);

        contentContainer.getChildren().addAll(titleLabel, tabPane, detailsBox, buttonBox);

        contentContainer.setOpacity(0);
        contentContainer.setTranslateY(-20);
        getChildren().add(contentContainer);

        buildButton.setOnAction(e -> {
            buildSelectedBuilding();
            close();
        });

        cancelButton.setOnAction(e -> close());

        initializeEventSubscriptions();
    }

    /**
     * Initialize subscriptions to relevant events.
     */
    private void initializeEventSubscriptions() {
        eventBus.subscribe(BuildingSelectedEvent.class, this::handleBuildingSelected);
    }

    /**
     * Handles building selection events from the UI.
     */
    private void handleBuildingSelected(BuildingSelectedEvent event) {
        Platform.runLater(() -> {
            selectedBuilding = event.getBuilding();
            updateDetailsSection();
            buildButton.setDisable(selectedBuilding == null);
        });
    }

    /**
     * Shows the overlay with animation.
     */
    public void show() {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), contentContainer);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(200), contentContainer);
        slideIn.setFromY(-20);
        slideIn.setToY(0);

        fadeIn.play();
        slideIn.play();
    }

    /**
     * Closes the overlay.
     */
    public void close() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), contentContainer);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> ScreenManager.getInstance().activateScreen(GameState.GAMEPLAY));

        TranslateTransition slideOut = new TranslateTransition(Duration.millis(200), contentContainer);
        slideOut.setFromY(0);
        slideOut.setToY(-20);

        fadeOut.play();
        slideOut.play();
    }

    /**
     * Creates the tab for production buildings.
     */
    private void createProductionBuildingsTab() {
        Tab tab = new Tab("Production");
        VBox content = new VBox(15);
        content.setPadding(new Insets(15));
        content.getStyleClass().add(AppTheme.STYLE_PANEL_CONTENT);

        Label resourceProducersLabel = new Label("Resource Producers");
        resourceProducersLabel.getStyleClass().add("building-category-header");
        resourceProducersLabel.setMaxWidth(Double.MAX_VALUE);

        VBox resourceProducersBox = new VBox(10);
        resourceProducersBox.setPadding(new Insets(5));

        for (ResourceProducer.ResourceProducerType type : ResourceProducer.ResourceProducerType.values()) {
            BuildingOptionButton button = new BuildingOptionButton(
                    type.getName(),
                    "Produces " + type.getOutputType().getName(),
                    () -> new ResourceProducer(type, tile)
            );

            resourceProducersBox.getChildren().add(button);
        }

        Label advancedProducersLabel = new Label("Advanced Producers");
        advancedProducersLabel.getStyleClass().add("building-category-header");
        advancedProducersLabel.setMaxWidth(Double.MAX_VALUE);

        VBox advancedProducersBox = new VBox(10);
        advancedProducersBox.setPadding(new Insets(5));

        for (AdvancedProducer.AdvancedProducerType type : AdvancedProducer.AdvancedProducerType.values()) {
            BuildingOptionButton button = new BuildingOptionButton(
                    type.getName(),
                    "Produces " + type.getOutputType().getName(),
                    () -> new AdvancedProducer(type, tile)
            );

            advancedProducersBox.getChildren().add(button);
        }

        content.getChildren().addAll(
                resourceProducersLabel,
                resourceProducersBox,
                new Separator(),
                advancedProducersLabel,
                advancedProducersBox
        );

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add(AppTheme.STYLE_PANEL);

        tab.setContent(scrollPane);
        tabPane.getTabs().add(tab);
    }

    /**
     * Creates the tab for habitation buildings.
     */
    private void createHabitationBuildingsTab() {
        Tab tab = new Tab("Habitation");
        VBox content = new VBox(15);
        content.setPadding(new Insets(15));
        content.getStyleClass().add(AppTheme.STYLE_PANEL_CONTENT);

        Label basicHousingLabel = new Label("Basic Housing");
        basicHousingLabel.getStyleClass().add("building-category-header");
        basicHousingLabel.setMaxWidth(Double.MAX_VALUE);

        VBox basicHousingBox = new VBox(10);
        basicHousingBox.setPadding(new Insets(5));

        for (BasicHousing.HousingType type : BasicHousing.HousingType.values()) {
            BuildingOptionButton button = new BuildingOptionButton(
                    type.getName(),
                    "Capacity: " + type.getCapacity() + " colonists",
                    () -> new BasicHousing(type, tile)
            );

            basicHousingBox.getChildren().add(button);
        }

        Label advancedHousingLabel = new Label("Advanced Housing");
        advancedHousingLabel.getStyleClass().add("building-category-header");
        advancedHousingLabel.setMaxWidth(Double.MAX_VALUE);

        VBox advancedHousingBox = new VBox(10);
        advancedHousingBox.setPadding(new Insets(5));

        for (AdvancedHousing.HousingType type : AdvancedHousing.HousingType.values()) {
            BuildingOptionButton button = new BuildingOptionButton(
                    type.getName(),
                    "Capacity: " + type.getCapacity() + " colonists",
                    () -> new AdvancedHousing(type, tile)
            );

            advancedHousingBox.getChildren().add(button);
        }

        content.getChildren().addAll(
                basicHousingLabel,
                basicHousingBox,
                new Separator(),
                advancedHousingLabel,
                advancedHousingBox
        );

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add(AppTheme.STYLE_PANEL);

        tab.setContent(scrollPane);
        tabPane.getTabs().add(tab);
    }

    /**
     * Updates the details section with information about the selected building.
     */
    private void updateDetailsSection() {
        detailsBox.getChildren().clear();

        if (selectedBuilding == null) {
            Label noSelectionLabel = new Label("Select a building to view details");
            noSelectionLabel.getStyleClass().add("details-empty-msg");
            detailsBox.getChildren().add(noSelectionLabel);
            return;
        }

        Label nameLabel = new Label(selectedBuilding.getName());
        nameLabel.getStyleClass().add("building-name");

        Label descriptionLabel = new Label(selectedBuilding.getDescription());
        descriptionLabel.getStyleClass().add("building-description");
        descriptionLabel.setWrapText(true);

        Label costLabel = new Label("Construction Cost:");
        costLabel.getStyleClass().add("building-name");
        costLabel.setStyle("-fx-font-size: 14px;");

        GridPane costGrid = new GridPane();
        costGrid.setHgap(10);
        costGrid.setVgap(5);

        int row = 0;
        boolean hasEnoughResources = true;

        for (Map.Entry<ResourceType, Integer> entry : selectedBuilding.getConstructionCost().entrySet()) {
            ResourceType type = entry.getKey();
            int cost = entry.getValue();
            int available = game.getResourceManager().getResource(type);

            Label typeLabel = new Label(type.getName() + ":");
            typeLabel.getStyleClass().add("building-description");

            Label valueLabel = new Label(cost + " / " + available);

            if (cost > available) {
                valueLabel.setTextFill(Color.RED);
                hasEnoughResources = false;
            } else {
                valueLabel.setTextFill(Color.LIGHTGREEN);
            }

            costGrid.add(typeLabel, 0, row);
            costGrid.add(valueLabel, 1, row);

            row++;
        }

        VBox specificDetails = new VBox(5);

        if (selectedBuilding instanceof ProductionBuilding productionBuilding) {
            Label outputLabel = new Label("Produces: " +
                    productionBuilding.getBaseOutputAmount() + " " +
                    productionBuilding.getPrimaryOutputType().getName() + " per turn");
            outputLabel.getStyleClass().add("building-description");
            specificDetails.getChildren().add(outputLabel);

            Label workersLabel = new Label("Workers required: " + productionBuilding.getWorkersRequired());
            workersLabel.getStyleClass().add("building-description");
            specificDetails.getChildren().add(workersLabel);
        }
        else if (selectedBuilding instanceof HabitationBuilding habitationBuilding) {
            Label capacityLabel = new Label("Capacity: " + habitationBuilding.getCapacity() + " colonists");
            capacityLabel.getStyleClass().add("building-description");
            specificDetails.getChildren().add(capacityLabel);

            if (habitationBuilding.getPopulationGrowthRate() > 0) {
                Label growthLabel = new Label("Natural growth: " +
                        habitationBuilding.getPopulationGrowthRate() + " colonists per turn");
                growthLabel.getStyleClass().add("building-description");
                specificDetails.getChildren().add(growthLabel);
            }
        }

        Label timeLabel = new Label("Construction time: " +
                selectedBuilding.getConstructionTime() + " turns");
        timeLabel.getStyleClass().add("building-description");

        Label warningLabel = null;
        if (!hasEnoughResources) {
            warningLabel = new Label("Not enough resources to build!");
            warningLabel.setTextFill(Color.RED);
            warningLabel.setStyle("-fx-font-weight: bold;");
        }

        detailsBox.getChildren().addAll(
                nameLabel,
                descriptionLabel,
                new Separator(),
                costLabel,
                costGrid,
                new Separator(),
                specificDetails,
                timeLabel
        );

        if (warningLabel != null) {
            detailsBox.getChildren().add(warningLabel);
        }

        buildButton.setDisable(!hasEnoughResources);
    }

    /**
     * Builds the selected building on the tile.
     */
    private void buildSelectedBuilding() {
        if (selectedBuilding == null || tile == null) {
            return;
        }

        Result<AbstractBuilding> result = game.getBuildingManager().constructBuilding(selectedBuilding);

        if (result.isSuccess()) {
            LOGGER.info("Successfully started construction of " + selectedBuilding.getName() +
                    " at " + tile);

            eventBus.publish(new TileEvents.TileUpdatedEvent(tile));

            Runnable showSuccessMessage = () -> {
                VBox messageBox = new VBox(10);
                messageBox.setAlignment(Pos.CENTER);
                messageBox.setPadding(new Insets(20));

                Label messageLabel = new Label("Construction started!");
                messageLabel.getStyleClass().add(AppTheme.STYLE_SUBTITLE);

                Label detailsLabel = new Label("Started building " + selectedBuilding.getName() +
                        " at tile " + tile.getX() + "," + tile.getY());
                detailsLabel.getStyleClass().add(AppTheme.STYLE_DESCRIPTION);

                ActionButton okButton = new ActionButton("OK", ActionButton.ButtonType.PRIMARY);
                okButton.setOnAction(e -> ((Stage) okButton.getScene().getWindow()).close());

                messageBox.getChildren().addAll(messageLabel, detailsLabel, okButton);

                new Thread(() -> {
                    try {
                        Thread.sleep(500);
                        Platform.runLater(() -> ScreenManager.getInstance().showDialog("Construction", messageBox));
                    } catch (InterruptedException ex) {
                        // Ignore
                    }
                }).start();
            };

            showSuccessMessage.run();

        } else {
            LOGGER.warning("Failed to construct building: " + result.getErrorMessage());

            VBox messageBox = new VBox(10);
            messageBox.setAlignment(Pos.CENTER);
            messageBox.setPadding(new Insets(20));

            Label messageLabel = new Label("Construction Failed");
            messageLabel.getStyleClass().add(AppTheme.STYLE_SUBTITLE);

            Label detailsLabel = new Label(result.getErrorMessage());
            detailsLabel.getStyleClass().add(AppTheme.STYLE_DESCRIPTION);

            ActionButton okButton = new ActionButton("OK", ActionButton.ButtonType.PRIMARY);
            okButton.setOnAction(e -> ((Stage) okButton.getScene().getWindow()).close());

            messageBox.getChildren().addAll(messageLabel, detailsLabel, okButton);

            ScreenManager.getInstance().showDialog("Construction Failed", messageBox);
        }
    }

    /**
     * Button for a building option in the selection list.
     */
    private class BuildingOptionButton extends HBox {
        private final AbstractBuilding building;

        public BuildingOptionButton(String name, String description, BuildingSupplier supplier) {
            setSpacing(10);
            setPadding(new Insets(10));
            setMinHeight(50);
            setPrefWidth(600);
            setAlignment(Pos.CENTER_LEFT);
            getStyleClass().addAll(AppTheme.STYLE_BUTTON, AppTheme.STYLE_PANEL);

            VBox textContent = new VBox(5);
            textContent.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(textContent, Priority.ALWAYS);

            Label nameLabel = new Label(name);
            nameLabel.getStyleClass().add(AppTheme.STYLE_SUBTITLE);

            Label descLabel = new Label(description);
            descLabel.getStyleClass().add(AppTheme.STYLE_DESCRIPTION);

            textContent.getChildren().addAll(nameLabel, descLabel);

            building = supplier.get();

            VBox infoBox = new VBox(3);
            infoBox.setAlignment(Pos.CENTER_RIGHT);
            infoBox.setMinWidth(120);

            if (building instanceof ProductionBuilding pb) {
                Label outputLabel = new Label(pb.getBaseOutputAmount() + " " +
                        pb.getPrimaryOutputType().getName() + "/turn");
                outputLabel.getStyleClass().add(AppTheme.STYLE_LABEL);
                outputLabel.setStyle("-fx-font-size: 11px;");
                infoBox.getChildren().add(outputLabel);
            }
            else if (building instanceof HabitationBuilding hb) {
                Label capacityLabel = new Label("Capacity: " + hb.getCapacity());
                capacityLabel.getStyleClass().add(AppTheme.STYLE_LABEL);
                capacityLabel.setStyle("-fx-font-size: 11px;");
                infoBox.getChildren().add(capacityLabel);
            }

            Label timeLabel = new Label(building.getConstructionTime() + " turns");
            timeLabel.getStyleClass().add(AppTheme.STYLE_LABEL);
            timeLabel.setStyle("-fx-font-size: 11px;");
            infoBox.getChildren().add(timeLabel);

            getChildren().addAll(textContent, infoBox);

            setOnMouseClicked(e -> {
                eventBus.publish(new BuildingSelectedEvent(building));

                getParent().getChildrenUnmodifiable().forEach(node -> {
                    if (node instanceof BuildingOptionButton) {
                        node.getStyleClass().remove("selected-building");
                        node.setStyle("-fx-background-color: " + AppTheme.toRgbString(AppTheme.COLOR_BACKGROUND_MEDIUM) + ";");
                    }
                });
                getStyleClass().add("selected-building");
                setStyle("-fx-background-color: " + AppTheme.toRgbString(AppTheme.COLOR_ACCENT_PRIMARY) + ";");
            });
        }
    }

    /**
     * Functional interface for creating building instances.
     */
    @FunctionalInterface
    private interface BuildingSupplier {
        AbstractBuilding get();
    }

    /**
     * Event fired when a building is selected in the overlay.
     */
    public static class BuildingSelectedEvent implements GameEvent {
        private final AbstractBuilding building;

        public BuildingSelectedEvent(AbstractBuilding building) {
            this.building = building;
        }

        public AbstractBuilding getBuilding() {
            return building;
        }

        @Override
        public String getName() {
            return "BuildingSelected";
        }
    }
}