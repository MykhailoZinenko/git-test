package com.colonygenesis.ui.components;

import com.colonygenesis.core.Game;
import com.colonygenesis.technology.*;
import com.colonygenesis.ui.ScreenManager;
import com.colonygenesis.ui.events.EventBus;
import com.colonygenesis.ui.styling.AppTheme;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.*;

/**
 * Overlay for displaying and interacting with the research tree.
 */
public class ResearchOverlay extends StackPane {
    private final Game game;
    private final TechManager techManager;
    private final ResearchTreeView treeView;
    private final TechDetailsPanel detailsPanel;
    private final VBox contentContainer;

    public ResearchOverlay(Game game) {
        this.game = game;
        this.techManager = game.getTechManager();

        setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        setAlignment(Pos.CENTER);

        contentContainer = new VBox(20);
        contentContainer.setAlignment(Pos.CENTER);
        contentContainer.setMaxWidth(1200);
        contentContainer.setMaxHeight(800);
        contentContainer.getStyleClass().add(AppTheme.STYLE_MENU_CONTAINER);
        contentContainer.setStyle("-fx-background-color: rgba(16, 20, 36, 0.95);");
        contentContainer.setPadding(new Insets(20));

        // Title
        Label titleLabel = new Label("Research Tree");
        titleLabel.getStyleClass().add(AppTheme.STYLE_TITLE);

        // Main content
        HBox mainContent = new HBox(20);
        mainContent.setPrefHeight(600);

        treeView = new ResearchTreeView(techManager);
        treeView.setPrefWidth(800);

        detailsPanel = new TechDetailsPanel(techManager);
        detailsPanel.setPrefWidth(350);

        mainContent.getChildren().addAll(treeView, detailsPanel);

        // Close button
        Button closeButton = new Button("Close");
        closeButton.getStyleClass().addAll(AppTheme.STYLE_BUTTON, AppTheme.STYLE_BUTTON_PRIMARY);
        closeButton.setOnAction(e -> close());

        contentContainer.getChildren().addAll(titleLabel, mainContent, closeButton);
        getChildren().add(contentContainer);

        // Subscribe to events
        EventBus.getInstance().subscribe(ResearchTreeView.TechSelectedEvent.class, event -> {
            detailsPanel.setTechnology(event.getTechnology());
        });

        EventBus.getInstance().subscribe(TechEvents.TechnologyResearchedEvent.class, event -> {
            treeView.refresh();
            if (detailsPanel.getCurrentTechnology() != null &&
                    detailsPanel.getCurrentTechnology().getId().equals(event.getTechnology().getId())) {
                detailsPanel.refresh();
            }
        });
    }

    public void show() {
        contentContainer.setOpacity(0);
        contentContainer.setTranslateY(-20);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), contentContainer);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(200), contentContainer);
        slideIn.setFromY(-20);
        slideIn.setToY(0);

        fadeIn.play();
        slideIn.play();
    }

    public void close() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), contentContainer);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            StackPane rootPane = ScreenManager.getInstance().getRootPane();
            rootPane.getChildren().remove(this);
        });

        TranslateTransition slideOut = new TranslateTransition(Duration.millis(200), contentContainer);
        slideOut.setFromY(0);
        slideOut.setToY(-20);

        fadeOut.play();
        slideOut.play();
    }
}