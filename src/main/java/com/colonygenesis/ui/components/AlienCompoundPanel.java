package com.colonygenesis.ui.components;

import com.colonygenesis.core.Game;
import com.colonygenesis.resource.AlienCompoundConverter;
import com.colonygenesis.resource.ResourceType;
import com.colonygenesis.ui.events.EventBus;
import com.colonygenesis.ui.events.NotificationEvents;
import com.colonygenesis.ui.styling.AppTheme;
import com.colonygenesis.util.Result;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.Map;

/**
 * Panel for managing alien compound conversions and special functions.
 */
public class AlienCompoundPanel extends GamePanel {
    private final Game game;
    private final AlienCompoundConverter converter;
    private Label statusLabel;
    private ComboBox<ResourceType> resourceSelector;
    private Spinner<Integer> amountSpinner;
    private GridPane conversionGrid;

    public AlienCompoundPanel(Game game) {
        super("Alien Compound Converter");
        this.game = game;
        this.converter = game.getAlienCompoundConverter();

        initializeUI();
        updateConversionRates();
        updateStatus();
    }

    private void initializeUI() {
        VBox contentBox = getContentArea();
        contentBox.setSpacing(15);
        contentBox.setPadding(new Insets(10));

        // Status display
        statusLabel = new Label("Available Alien Compounds: 0");
        statusLabel.getStyleClass().add(AppTheme.STYLE_LABEL);
        statusLabel.setTextFill(Color.MAGENTA);

        // Resource conversion section
        Label conversionLabel = new Label("Resource Conversion:");
        conversionLabel.getStyleClass().add(AppTheme.STYLE_SUBTITLE);

        // Resource selector
        resourceSelector = new ComboBox<>();
        resourceSelector.getItems().addAll(
                ResourceType.ENERGY,
                ResourceType.FOOD,
                ResourceType.WATER,
                ResourceType.MATERIALS,
                ResourceType.RESEARCH
        );
        resourceSelector.setValue(ResourceType.ENERGY);
        resourceSelector.getStyleClass().add(AppTheme.STYLE_COMBO_BOX);

        // Amount spinner
        amountSpinner = new Spinner<>(1, 100, 1);
        amountSpinner.setEditable(true);
        amountSpinner.setPrefWidth(100);

        // Convert button
        Button convertButton = new Button("Convert");
        convertButton.getStyleClass().addAll(AppTheme.STYLE_BUTTON, AppTheme.STYLE_BUTTON_PRIMARY);
        convertButton.setOnAction(e -> handleConversion());

        HBox conversionControls = new HBox(10);
        conversionControls.getChildren().addAll(resourceSelector, amountSpinner, convertButton);

        // Conversion rates display
        conversionGrid = new GridPane();
        conversionGrid.setHgap(10);
        conversionGrid.setVgap(5);

        // Research boost section
        Label boostLabel = new Label("Research Boost:");
        boostLabel.getStyleClass().add(AppTheme.STYLE_SUBTITLE);

        Spinner<Integer> boostSpinner = new Spinner<>(1, 50, 1);
        boostSpinner.setEditable(true);
        boostSpinner.setPrefWidth(100);

        Button boostButton = new Button("Boost Research");
        boostButton.getStyleClass().addAll(AppTheme.STYLE_BUTTON, AppTheme.STYLE_BUTTON_SUCCESS);
        boostButton.setOnAction(e -> handleResearchBoost(boostSpinner.getValue()));

        HBox boostControls = new HBox(10);
        boostControls.getChildren().addAll(boostSpinner, boostButton);

        contentBox.getChildren().addAll(
                statusLabel,
                conversionLabel,
                conversionControls,
                conversionGrid,
                boostLabel,
                boostControls
        );
    }

    private void updateConversionRates() {
        conversionGrid.getChildren().clear();

        Map<ResourceType, Integer> rates = converter.getAllConversionRates();
        int row = 0;

        for (Map.Entry<ResourceType, Integer> entry : rates.entrySet()) {
            Label resourceLabel = new Label(entry.getKey().getName() + ":");
            resourceLabel.getStyleClass().add(AppTheme.STYLE_LABEL);

            Label rateLabel = new Label("1 → " + entry.getValue());
            rateLabel.getStyleClass().add(AppTheme.STYLE_LABEL);
            rateLabel.setTextFill(Color.LIGHTGREEN);

            conversionGrid.add(resourceLabel, 0, row);
            conversionGrid.add(rateLabel, 1, row);
            row++;
        }
    }

    private void handleConversion() {
        ResourceType targetResource = resourceSelector.getValue();
        int amount = amountSpinner.getValue();

        if (targetResource != null) {
            Result<Integer> result = converter.convert(amount, targetResource);

            if (result.isFailure()) {
                EventBus.getInstance().publish(NotificationEvents.Factory.error(
                        "Conversion Failed",
                        result.getErrorMessage()
                ));
            }

            updateStatus();
        }
    }

    private void handleResearchBoost(int amount) {
        Result<Integer> result = converter.boostResearch(amount);

        if (result.isFailure()) {
            EventBus.getInstance().publish(NotificationEvents.Factory.error(
                    "Research Boost Failed",
                    result.getErrorMessage()
            ));
        }

        updateStatus();
    }

    /**
     * Updates the status display with current alien compound amount.
     */
    public void updateStatus() {
        int available = game.getResourceManager().getResource(ResourceType.ALIEN_COMPOUNDS);
        statusLabel.setText("Available Alien Compounds: " + available);
    }
}