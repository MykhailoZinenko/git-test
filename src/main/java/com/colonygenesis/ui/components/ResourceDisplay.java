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
        String text;
        if (resourceType.isStorable()) {
            text = amount + "/" + capacity;
        } else {
            text = String.valueOf(amount);
        }

        if (production > 0) {
            text += " (+)";
            valueLabel.getStyleClass().removeAll(
                    AppTheme.STYLE_RESOURCE_NEGATIVE,
                    AppTheme.STYLE_RESOURCE_NEUTRAL
            );
            valueLabel.getStyleClass().add(AppTheme.STYLE_RESOURCE_POSITIVE);
        } else if (production < 0) {
            text += " (-)";
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

        Tooltip tooltip = new Tooltip(
                resourceType.getName() + "\n" +
                        "Current: " + amount + "\n" +
                        (resourceType.isStorable() ? "Capacity: " + capacity + "\n" : "") +
                        "Production: " + (production > 0 ? "+" : "") + production
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
}