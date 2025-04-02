package com.colonygenesis.ui.components;

import com.colonygenesis.resource.ResourceType;
import com.colonygenesis.ui.styling.AppTheme;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/**
 * Component for displaying a single resource with icon and value.
 */
public class ResourceDisplay extends HBox {
    private final ResourceType resourceType;
    private final Label valueLabel;
    private final Circle resourceIcon;
    private int currentAmount = 0;
    private int currentCapacity = 0;
    private int currentProduction = 0;

    /**
     * Creates a new resource display for the specified resource type.
     *
     * @param resourceType The resource type to display
     */
    public ResourceDisplay(ResourceType resourceType) {
        this.resourceType = resourceType;
        getStyleClass().add(AppTheme.STYLE_RESOURCE_ITEM);

        // Create resource icon
        resourceIcon = new Circle(8);
        resourceIcon.setFill(resourceType.getColor());
        resourceIcon.getStyleClass().add(AppTheme.STYLE_RESOURCE_ICON);

        valueLabel = new Label("0");
        valueLabel.getStyleClass().add(AppTheme.STYLE_RESOURCE_LABEL);

        Tooltip tooltip = new Tooltip(resourceType.getName() + "\n" + resourceType.getDescription());
        Tooltip.install(this, tooltip);

        getChildren().addAll(resourceIcon, valueLabel);
    }

    /**
     * Updates the resource display with new values.
     *
     * @param amount The current resource amount
     * @param capacity The storage capacity for the resource
     * @param production The production rate
     */
    public void update(int amount, int capacity, int production) {
        // Store current values for access by other methods
        currentAmount = amount;
        currentCapacity = capacity;
        currentProduction = production;

        String text;
        if (resourceType.isStorable()) {
            text = amount + "/" + capacity;
        } else {
            text = String.valueOf(amount);
        }

        // Add production/consumption indicator
        if (production > 0) {
            text += " (+" + production + ")";
            valueLabel.getStyleClass().removeAll(
                    AppTheme.STYLE_RESOURCE_NEGATIVE,
                    AppTheme.STYLE_RESOURCE_NEUTRAL
            );
            valueLabel.getStyleClass().add(AppTheme.STYLE_RESOURCE_POSITIVE);
        } else if (production < 0) {
            text += " (" + production + ")";
            valueLabel.getStyleClass().removeAll(
                    AppTheme.STYLE_RESOURCE_POSITIVE,
                    AppTheme.STYLE_RESOURCE_NEUTRAL
            );
            valueLabel.getStyleClass().add(AppTheme.STYLE_RESOURCE_NEGATIVE);
        } else {
            valueLabel.getStyleClass().removeAll(
                    AppTheme.STYLE_RESOURCE_POSITIVE,
                    AppTheme.STYLE_RESOURCE_NEGATIVE
            );
            valueLabel.getStyleClass().add(AppTheme.STYLE_RESOURCE_NEUTRAL);
        }

        valueLabel.setText(text);

        // Enhanced tooltip
        Tooltip tooltip = new Tooltip(
                resourceType.getName() + "\n" +
                        "Current: " + amount + "\n" +
                        (resourceType.isStorable() ? "Capacity: " + capacity + "\n" : "") +
                        (production > 0 ? "Production: +" + production + "/turn" :
                                production < 0 ? "Consumption: " + (-production) + "/turn" : "Net: 0/turn")
        );
        Tooltip.install(this, tooltip);
    }

    /**
     * Gets the resource type displayed by this component.
     *
     * @return The resource type
     */
    public ResourceType getResourceType() {
        return resourceType;
    }

    /**
     * Gets the current amount of the resource.
     *
     * @return The current amount
     */
    public int getAmount() {
        return currentAmount;
    }

    /**
     * Gets the current capacity for the resource.
     *
     * @return The current capacity
     */
    public int getCapacity() {
        return currentCapacity;
    }

    /**
     * Gets the current production rate for the resource.
     *
     * @return The current production rate
     */
    public int getProduction() {
        return currentProduction;
    }
}