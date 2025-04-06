package com.colonygenesis.ui.components;

import com.colonygenesis.resource.ResourceType;
import com.colonygenesis.ui.styling.AppTheme;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

/**
 * Component for displaying a single resource with icon and value.
 */
public class ResourceDisplay extends HBox {
    private final ResourceType resourceType;
    private final Label valueLabel;
    private int currentAmount = 0;
    private int currentCapacity = 0;
    private int currentProduction = 0;

    private int availableWorkers = 0;
    private int assignedWorkers = 0;
    private boolean showWorkersInfo = false;
    private Label workerInfoLabel;

    /**
     * Creates a new resource display for the specified resource type.
     *
     * @param resourceType The resource type to display
     */
    public ResourceDisplay(ResourceType resourceType) {
        this.resourceType = resourceType;
        getStyleClass().add(AppTheme.STYLE_RESOURCE_ITEM);

        // Create resource icon
        Circle resourceIcon = new Circle(8);
        resourceIcon.setFill(resourceType.getColor());
        resourceIcon.getStyleClass().add(AppTheme.STYLE_RESOURCE_ICON);

        if (resourceType.isPopulation()) {
            VBox labelsBox = new VBox(2);

            valueLabel = new Label("0");
            valueLabel.getStyleClass().add(AppTheme.STYLE_RESOURCE_LABEL);

            workerInfoLabel = new Label("");
            workerInfoLabel.getStyleClass().add("worker-info-label");
            workerInfoLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: rgb(200, 200, 200);");
            workerInfoLabel.setVisible(false);

            labelsBox.getChildren().addAll(valueLabel, workerInfoLabel);
            getChildren().addAll(resourceIcon, labelsBox);
        } else {
            valueLabel = new Label("0");
            valueLabel.getStyleClass().add(AppTheme.STYLE_RESOURCE_LABEL);
            getChildren().addAll(resourceIcon, valueLabel);
        }

        Tooltip tooltip = new Tooltip(resourceType.getName() + "\n" + resourceType.getDescription());
        Tooltip.install(this, tooltip);
    }

    /**
     * Updates the resource display with new values.
     *
     * @param amount The current resource amount
     * @param capacity The storage capacity for the resource
     * @param production The production rate
     */
    public void update(int amount, int capacity, int production) {
        currentAmount = amount;
        currentCapacity = capacity;
        currentProduction = production;

        String text;
        if (resourceType.isStorable()) {
            text = amount + "/" + capacity;
        } else {
            text = String.valueOf(amount);
        }

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

        String tooltipText = resourceType.getName() + "\n" +
                "Current: " + amount + "\n" +
                (resourceType.isStorable() ? "Capacity: " + capacity + "\n" : "") +
                (production > 0 ? "Production: +" + production + "/turn" :
                        production < 0 ? "Consumption: " + (-production) + "/turn" : "Net: 0/turn");

        if (resourceType.isPopulation() && showWorkersInfo) {
            tooltipText += "\nAvailable Workers: " + availableWorkers +
                    "\nAssigned Workers: " + assignedWorkers;
        }

        Tooltip.install(this, new Tooltip(tooltipText));
    }

    /**
     * Sets whether to show worker information for population resource.
     *
     * @param show True to show worker info, false to hide
     */
    public void setShowWorkersInfo(boolean show) {
        this.showWorkersInfo = show;
        if (resourceType.isPopulation() && workerInfoLabel != null) {
            workerInfoLabel.setVisible(show);
        }
    }

    /**
     * Updates the worker information for population resource.
     *
     * @param available Available workers
     * @param assigned Assigned workers
     */
    public void updateWorkerInfo(int available, int assigned) {
        this.availableWorkers = available;
        this.assignedWorkers = assigned;

        if (resourceType.isPopulation() && workerInfoLabel != null && showWorkersInfo) {
            workerInfoLabel.setText("Available: " + available + " | Assigned: " + assigned);

            String tooltipText = resourceType.getName() + "\n" +
                    "Current: " + currentAmount + "\n" +
                    "Capacity: " + currentCapacity + "\n" +
                    "Available Workers: " + available + "\n" +
                    "Assigned Workers: " + assigned;

            Tooltip.install(this, new Tooltip(tooltipText));
        }
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

    /**
     * Gets the number of available workers.
     * Only relevant for population resource.
     *
     * @return The number of available workers
     */
    public int getAvailableWorkers() {
        return availableWorkers;
    }

    /**
     * Gets the number of assigned workers.
     * Only relevant for population resource.
     *
     * @return The number of assigned workers
     */
    public int getAssignedWorkers() {
        return assignedWorkers;
    }
}