package com.colonygenesis.ui.components;

import com.colonygenesis.ui.styling.AppTheme;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Base panel component for the game UI.
 * Provides a consistent look and feel for panel-style UI elements.
 */
public class GamePanel extends VBox {
    private final Label titleLabel;
    private final VBox contentArea;

    /**
     * Creates a new game panel with the specified title.
     *
     * @param title The panel title
     */
    public GamePanel(String title) {
        getStyleClass().add(AppTheme.STYLE_PANEL);
        setSpacing(5);
        setPadding(new Insets(10));

        titleLabel = new Label(title);
        titleLabel.getStyleClass().add(AppTheme.STYLE_PANEL_TITLE);

        contentArea = new VBox(10);
        contentArea.getStyleClass().add(AppTheme.STYLE_PANEL_CONTENT);

        getChildren().addAll(titleLabel, contentArea);
    }

    /**
     * Sets the panel title.
     *
     * @param title The new title
     */
    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    /**
     * Gets the content area to add child nodes.
     *
     * @return The content VBox
     */
    public VBox getContentArea() {
        return contentArea;
    }

    /**
     * Adds content to the panel.
     *
     * @param node The node to add
     */
    public void addContent(Node node) {
        contentArea.getChildren().add(node);
    }

    /**
     * Clears all content from the panel.
     */
    public void clearContent() {
        contentArea.getChildren().clear();
    }
}