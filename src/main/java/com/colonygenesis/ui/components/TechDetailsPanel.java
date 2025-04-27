package com.colonygenesis.ui.components;

import com.colonygenesis.technology.TechManager;
import com.colonygenesis.technology.Technology;
import com.colonygenesis.ui.components.GamePanel;
import com.colonygenesis.ui.styling.AppTheme;
import com.colonygenesis.util.Result;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.Map;

/**
 * Panel for displaying technology details.
 */
public class TechDetailsPanel extends GamePanel {
    private final TechManager techManager;
    private final VBox contentBox;
    private final Button researchButton;
    private Technology currentTech;

    public TechDetailsPanel(TechManager techManager) {
        super("Technology Details");
        this.techManager = techManager;

        contentBox = getContentArea();
        contentBox.setSpacing(10);

        // Default message
        Label defaultLabel = new Label("Select a technology to view details");
        defaultLabel.getStyleClass().add(AppTheme.STYLE_LABEL);
        contentBox.getChildren().add(defaultLabel);

        // Research button
        researchButton = new Button("Research");
        researchButton.getStyleClass().addAll(AppTheme.STYLE_BUTTON, AppTheme.STYLE_BUTTON_SUCCESS);
        researchButton.setVisible(false);
        researchButton.setOnAction(e -> researchCurrentTech());

        contentBox.getChildren().add(researchButton);
    }

    public void setTechnology(Technology tech) {
        this.currentTech = tech;
        refresh();
    }

    public void refresh() {
        contentBox.getChildren().clear();

        if (currentTech == null) {
            Label defaultLabel = new Label("Select a technology to view details");
            defaultLabel.getStyleClass().add(AppTheme.STYLE_LABEL);
            contentBox.getChildren().add(defaultLabel);
            researchButton.setVisible(false);
            return;
        }

        // Tech name
        Label nameLabel = new Label(currentTech.getName());
        nameLabel.getStyleClass().add(AppTheme.STYLE_SUBTITLE);

        // Tech description
        Label descLabel = new Label(currentTech.getDescription());
        descLabel.getStyleClass().add(AppTheme.STYLE_LABEL);
        descLabel.setWrapText(true);

        // Branch and tier
        Label branchLabel = new Label("Branch: " + currentTech.getBranch().getName());
        branchLabel.getStyleClass().add(AppTheme.STYLE_LABEL);
        Label tierLabel = new Label("Tier: " + currentTech.getTier());
        tierLabel.getStyleClass().add(AppTheme.STYLE_LABEL);

        contentBox.getChildren().addAll(nameLabel, descLabel, branchLabel, tierLabel);

        // Prerequisites
        if (!currentTech.getTechPrerequisites().isEmpty()) {
            Label prereqLabel = new Label("Prerequisites:");
            prereqLabel.getStyleClass().add(AppTheme.STYLE_LABEL);
            prereqLabel.setStyle("-fx-font-weight: bold;");
            contentBox.getChildren().add(prereqLabel);

            for (String prereqId : currentTech.getTechPrerequisites()) {
                Technology prereq = techManager.getTechTree().getTechnology(prereqId);
                if (prereq != null) {
                    boolean researched = techManager.isTechResearched(prereqId);
                    Label prereqItemLabel = new Label("• " + prereq.getName());
                    prereqItemLabel.getStyleClass().add(AppTheme.STYLE_LABEL);
                    prereqItemLabel.setTextFill(researched ? Color.GREEN : Color.RED);
                    contentBox.getChildren().add(prereqItemLabel);
                }
            }
        }

        // Resource costs
        if (!currentTech.getResourceCosts().isEmpty()) {
            Label costLabel = new Label("Cost:");
            costLabel.getStyleClass().add(AppTheme.STYLE_LABEL);
            costLabel.setStyle("-fx-font-weight: bold;");
            contentBox.getChildren().add(costLabel);

            for (Map.Entry<com.colonygenesis.resource.ResourceType, Integer> entry :
                    currentTech.getResourceCosts().entrySet()) {
                int available = techManager.getGame().getResourceManager().getResource(entry.getKey());
                Label costItemLabel = new Label("• " + entry.getKey().getName() + ": " +
                        entry.getValue() + " / " + available);
                costItemLabel.getStyleClass().add(AppTheme.STYLE_LABEL);
                costItemLabel.setTextFill(available >= entry.getValue() ? Color.GREEN : Color.RED);
                contentBox.getChildren().add(costItemLabel);
            }
        }

        // Research button
        boolean researched = techManager.isTechResearched(currentTech.getId());
        boolean canResearch = techManager.canResearch(currentTech);

        if (researched) {
            Label researchedLabel = new Label("RESEARCHED");
            researchedLabel.getStyleClass().add(AppTheme.STYLE_LABEL);
            researchedLabel.setTextFill(Color.GREEN);
            researchedLabel.setStyle("-fx-font-weight: bold;");
            contentBox.getChildren().add(researchedLabel);
            researchButton.setVisible(false);
        } else {
            researchButton.setVisible(true);
            researchButton.setDisable(!canResearch);
            contentBox.getChildren().add(researchButton);
        }
    }

    private void researchCurrentTech() {
        if (currentTech == null) return;

        Result<Technology> result = techManager.researchTechnology(currentTech.getId());
        if (result.isFailure()) {
            // Show error message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Research Failed");
            alert.setHeaderText(null);
            alert.setContentText(result.getErrorMessage());
            alert.showAndWait();
        }
    }

    public Technology getCurrentTechnology() {
        return currentTech;
    }
}