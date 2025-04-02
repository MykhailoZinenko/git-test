package com.colonygenesis.ui.components;

import com.colonygenesis.resource.ResourceType;
import com.colonygenesis.ui.events.ColonyEvents;
import com.colonygenesis.ui.events.EventBus;
import com.colonygenesis.ui.events.ResourceEvents;
import com.colonygenesis.ui.styling.AppTheme;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

import java.util.EnumMap;
import java.util.Map;

/**
 * Component for displaying resources in the game UI.
 * Uses the ResourceDisplay component for individual resources.
 */
public class ResourceBar extends HBox {
    private final Map<ResourceType, ResourceDisplay> resourceDisplays = new EnumMap<>(ResourceType.class);
    private final Label populationLabel;
    private final EventBus eventBus;

    /**
     * Creates a new resource bar.
     */
    public ResourceBar() {
        getStyleClass().add(AppTheme.STYLE_RESOURCE_BAR);
        this.eventBus = EventBus.getInstance();

        // Add population display
        HBox populationDisplay = new HBox(5);
        populationDisplay.setAlignment(Pos.CENTER_LEFT);

        Label populationIcon = new Label("👥"); // Unicode people icon
        populationIcon.setStyle("-fx-font-size: 16px;");

        populationLabel = new Label("0");
        populationLabel.getStyleClass().add(AppTheme.STYLE_RESOURCE_LABEL);

        populationDisplay.getChildren().addAll(populationIcon, populationLabel);

        getChildren().add(populationDisplay);

        // Initialize resource indicators
        initializeResourceIndicators();

        // Subscribe to resource events
        subscribeToEvents();
    }

    /**
     * Initializes the resource indicators.
     */
    private void initializeResourceIndicators() {
        for (ResourceType type : ResourceType.values()) {
            if (!type.isBasic()) continue;

            ResourceDisplay resourceDisplay = new ResourceDisplay(type);
            resourceDisplays.put(type, resourceDisplay);
            getChildren().add(resourceDisplay);
        }
    }

    /**
     * Subscribes to relevant events for reactive updates.
     */
    private void subscribeToEvents() {
        // Subscribe to resource updates
        eventBus.subscribe(ResourceEvents.ResourcesUpdatedEvent.class, this::handleResourcesUpdated);
        eventBus.subscribe(ResourceEvents.ResourceChangedEvent.class, this::handleResourceChanged);

        // Subscribe to population updates
        eventBus.subscribe(ColonyEvents.PopulationChangedEvent.class, this::handlePopulationChanged);
    }

    /**
     * Handles updates to multiple resources at once.
     */
    private void handleResourcesUpdated(ResourceEvents.ResourcesUpdatedEvent event) {
        Platform.runLater(() -> {
            update(event.getResources(), event.getProduction(), event.getCapacity());
        });
    }

    /**
     * Handles updates to a single resource.
     */
    private void handleResourceChanged(ResourceEvents.ResourceChangedEvent event) {
        Platform.runLater(() -> {
            ResourceType type = event.getResourceType();
            ResourceDisplay display = resourceDisplays.get(type);

            if (display != null) {
                // We need the production and capacity values as well
                // This is a partial update, so we'll need to get the current values
                Map<ResourceType, Integer> resources = new EnumMap<>(ResourceType.class);
                resources.put(type, event.getNewAmount());

                // For production, check if this is a production update
                Map<ResourceType, Integer> production = new EnumMap<>(ResourceType.class);
                if (event.isProduction()) {
                    production.put(type, event.getNewAmount());
                } else {
                    // Use the existing production value
                    production.put(type, display.getProduction());
                }

                // For capacity, use the existing capacity value
                Map<ResourceType, Integer> capacity = new EnumMap<>(ResourceType.class);
                capacity.put(type, display.getCapacity());

                update(resources, production, capacity);
            }
        });
    }

    /**
     * Handles changes to the colony population.
     */
    private void handlePopulationChanged(ColonyEvents.PopulationChangedEvent event) {
        Platform.runLater(() -> {
            updatePopulation(event.getTotalPopulation(), event.getHousingCapacity(), event.getAvailableWorkers());
        });
    }

    /**
     * Updates the resource display with the current game state.
     *
     * @param resources The current resource amounts
     * @param production The production rates
     * @param capacities The storage capacities
     */
    public void update(Map<ResourceType, Integer> resources,
                       Map<ResourceType, Integer> production,
                       Map<ResourceType, Integer> capacities) {
        for (ResourceType type : ResourceType.values()) {
            ResourceDisplay display = resourceDisplays.get(type);
            if (display == null) continue;

            int amount = resources.getOrDefault(type, 0);
            int net = production.getOrDefault(type, 0);
            int capacity = capacities.getOrDefault(type, 0);

            display.update(amount, capacity, net);
        }
    }

    /**
     * Updates the population display with current values.
     *
     * @param currentPopulation The current population
     * @param capacity The housing capacity
     * @param availableWorkers The number of available workers
     */
    public void updatePopulation(int currentPopulation, int capacity, int availableWorkers) {
        populationLabel.setText(currentPopulation + "/" + capacity + " (" + availableWorkers + " free)");

        // Color based on capacity usage
        double ratio = (double) currentPopulation / capacity;
        if (ratio < 0.5) {
            populationLabel.getStyleClass().removeAll(
                    AppTheme.STYLE_RESOURCE_NEGATIVE,
                    AppTheme.STYLE_RESOURCE_POSITIVE
            );
            populationLabel.getStyleClass().add(AppTheme.STYLE_RESOURCE_NEUTRAL);
        } else if (ratio < 0.9) {
            populationLabel.getStyleClass().removeAll(
                    AppTheme.STYLE_RESOURCE_NEGATIVE,
                    AppTheme.STYLE_RESOURCE_NEUTRAL
            );
            populationLabel.getStyleClass().add(AppTheme.STYLE_RESOURCE_POSITIVE);
        } else {
            populationLabel.getStyleClass().removeAll(
                    AppTheme.STYLE_RESOURCE_POSITIVE,
                    AppTheme.STYLE_RESOURCE_NEUTRAL
            );
            populationLabel.getStyleClass().add(AppTheme.STYLE_RESOURCE_NEGATIVE);
        }

        // Add tooltip with more details
        Tooltip tooltip = new Tooltip(
                "Total Population: " + currentPopulation + "\n" +
                        "Housing Capacity: " + capacity + "\n" +
                        "Available Workers: " + availableWorkers
        );
        Tooltip.install(populationLabel, tooltip);
    }
}