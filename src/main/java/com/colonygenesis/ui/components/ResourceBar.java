package com.colonygenesis.ui.components;

import com.colonygenesis.resource.ResourceType;
import com.colonygenesis.ui.events.EventBus;
import com.colonygenesis.ui.events.ResourceEvents;
import com.colonygenesis.ui.styling.AppTheme;
import javafx.application.Platform;
import javafx.scene.layout.HBox;

import java.util.EnumMap;
import java.util.Map;

/**
 * Component for displaying resources in the game UI.
 * Uses the ResourceDisplay component for individual resources.
 */
public class ResourceBar extends HBox {
    private final Map<ResourceType, ResourceDisplay> resourceDisplays = new EnumMap<>(ResourceType.class);
    private final ResourceDisplay populationDisplay;
    private final EventBus eventBus;

    /**
     * Creates a new resource bar.
     */
    public ResourceBar() {
        getStyleClass().add(AppTheme.STYLE_RESOURCE_BAR);
        this.eventBus = EventBus.getInstance();

        initializeResourceIndicators();

        populationDisplay = resourceDisplays.get(ResourceType.POPULATION);

        subscribeToEvents();
    }

    /**
     * Initializes the resource indicators.
     */
    private void initializeResourceIndicators() {
        ResourceDisplay populationDisplay = new ResourceDisplay(ResourceType.POPULATION);
        populationDisplay.setShowWorkersInfo(true); // Enable additional worker info
        resourceDisplays.put(ResourceType.POPULATION, populationDisplay);
        getChildren().add(populationDisplay);

        for (ResourceType type : ResourceType.values()) {
            if (type == ResourceType.POPULATION) continue;

            ResourceDisplay resourceDisplay = new ResourceDisplay(type);
            resourceDisplays.put(type, resourceDisplay);
            getChildren().add(resourceDisplay);
        }
    }

    /**
     * Subscribes to relevant events for reactive updates.
     */
    private void subscribeToEvents() {
        eventBus.subscribe(ResourceEvents.ResourcesUpdatedEvent.class, this::handleResourcesUpdated);
        eventBus.subscribe(ResourceEvents.ResourceChangedEvent.class, this::handleResourceChanged);

        eventBus.subscribe(ResourceEvents.PopulationChangedEvent.class, this::handlePopulationChanged);
        eventBus.subscribe(ResourceEvents.WorkerAvailabilityChangedEvent.class, this::handleWorkerAvailabilityChanged);
    }

    /**
     * Handles updates to multiple resources at once.
     */
    private void handleResourcesUpdated(ResourceEvents.ResourcesUpdatedEvent event) {
        Platform.runLater(() -> {
            update(event.getResources(), event.getProduction(), event.getCapacity());

            if (populationDisplay != null) {
                populationDisplay.updateWorkerInfo(
                        event.getAvailableWorkers(),
                        event.getAssignedWorkers()
                );
            }
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
                update(type, event.getNewAmount(), display.getCapacity(), display.getProduction());
            }
        });
    }

    /**
     * Handles changes to the colony population.
     */
    private void handlePopulationChanged(ResourceEvents.PopulationChangedEvent event) {
        Platform.runLater(() -> {
            if (populationDisplay != null) {
                populationDisplay.update(
                        event.getTotalPopulation(),
                        event.getHousingCapacity(),
                        0 // Production is handled separately
                );
            }
        });
    }

    /**
     * Handles changes to worker availability.
     */
    private void handleWorkerAvailabilityChanged(ResourceEvents.WorkerAvailabilityChangedEvent event) {
        Platform.runLater(() -> {
            if (populationDisplay != null) {
                populationDisplay.updateWorkerInfo(
                        event.getAvailableWorkers(),
                        populationDisplay.getAmount() - event.getAvailableWorkers()
                );
            }
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
     * Updates a specific resource display.
     *
     * @param type The resource type
     * @param amount The current amount
     * @param capacity The storage capacity
     * @param production The production rate
     */
    public void update(ResourceType type, int amount, int capacity, int production) {
        ResourceDisplay display = resourceDisplays.get(type);
        if (display != null) {
            display.update(amount, capacity, production);
        }
    }

    /**
     * Cleans up resources when the component is no longer needed.
     */
    public void dispose() {
        eventBus.unsubscribeAll(this);
        resourceDisplays.clear();
    }
}