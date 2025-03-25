package com.colonygenesis.ui.components;

import com.colonygenesis.resource.ResourceType;
import com.colonygenesis.ui.styling.AppTheme;
import javafx.scene.layout.HBox;

import java.util.EnumMap;
import java.util.Map;

/**
 * Component for displaying resources in the game UI.
 * Uses the ResourceDisplay component for individual resources.
 */
public class ResourceBar extends HBox {
    private final Map<ResourceType, ResourceDisplay> resourceDisplays = new EnumMap<>(ResourceType.class);

    /**
     * Creates a new resource bar.
     */
    public ResourceBar() {
        getStyleClass().add(AppTheme.STYLE_RESOURCE_BAR);
        initializeResourceIndicators();
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
}