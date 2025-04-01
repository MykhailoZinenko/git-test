package com.colonygenesis.ui.components;

import com.colonygenesis.map.Tile;
import com.colonygenesis.resource.ResourceType;
import com.colonygenesis.ui.events.EventBus;
import com.colonygenesis.ui.events.TileEvents;
import com.colonygenesis.ui.styling.AppTheme;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Map;

/**
 * Panel that displays information about a selected tile.
 */
public class TileInfoPanel extends GamePanel {

    private final VBox contentBox;
    private final Label titleLabel;
    private final Label positionLabel;
    private final Label descriptionLabel;
    private final Label statusLabel;
    private final VBox resourcesBox;
    private final Button colonizeButton;

    private Tile selectedTile;
    private Map<ResourceType, Integer> colonizationCost;
    private final EventBus eventBus = EventBus.getInstance();

    /**
     * Constructs a new tile info panel.
     */
    public TileInfoPanel() {
        super("Tile Information");

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

        contentBox.getChildren().addAll(
                titleLabel,
                positionLabel,
                new Separator(),
                descriptionLabel,
                new Separator(),
                statusLabel,
                resourcesBox,
                colonizeButton
        );

        eventBus.subscribe(TileEvents.TileSelectedEvent.class, this::handleTileSelected);

        eventBus.subscribe(TileEvents.TileUpdatedEvent.class, this::handleTileUpdated);
    }

    /**
     * Handles tile selection events.
     */
    private void handleTileSelected(TileEvents.TileSelectedEvent event) {
        setTile(event.getTile());
    }

    /**
     * Handles tile updated events.
     */
    private void handleTileUpdated(TileEvents.TileUpdatedEvent event) {
        if (selectedTile != null && event.getTile() != null &&
                selectedTile.getX() == event.getTile().getX() &&
                selectedTile.getY() == event.getTile().getY()) {

            setTile(event.getTile());
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
            return;
        }

        titleLabel.setText(selectedTile.getTerrainType().getName());
        positionLabel.setText("Position: " + selectedTile.getX() + ", " + selectedTile.getY());
        descriptionLabel.setText(selectedTile.getTerrainType().getDescription());

        if (selectedTile.isColonized()) {
            statusLabel.setText("Status: Colonized");
            colonizeButton.setVisible(false);
        } else if (!selectedTile.isHabitable()) {
            statusLabel.setText("Status: Not Habitable");
            colonizeButton.setVisible(false);
        } else {
            statusLabel.setText("Status: Not Colonized");
            colonizeButton.setVisible(true);
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
}