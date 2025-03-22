package com.colonygenesis.ui;

import com.colonygenesis.ui.styling.AppTheme;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class MainMenuScreen extends BorderPane {

    public MainMenuScreen() {
        initializeUI();
    }

    private void initializeUI() {
        Label titleLabel = new Label("Exoplanet: Colony Genesis");
        titleLabel.getStyleClass().add(AppTheme.STYLE_TITLE);

        Button newGameBtn = createMenuButton("New Game");
        Button loadGameBtn = createMenuButton("Load Game");
        Button settingsBtn = createMenuButton("Settings");
        Button exitBtn = createMenuButton("Exit");
        exitBtn.getStyleClass().add("danger-button");

        newGameBtn.setOnAction(e -> System.out.println("New Game requested"));
        loadGameBtn.setOnAction(e -> System.out.println("Load Game requested"));
        settingsBtn.setOnAction(e -> System.out.println("Settings requested"));
        exitBtn.setOnAction(e -> System.exit(0));

        VBox menuBox = new VBox(20);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.getChildren().addAll(titleLabel, newGameBtn, loadGameBtn, settingsBtn, exitBtn);

        setCenter(menuBox);

        setStyle("-fx-background-color: linear-gradient(to bottom, #0d1b2a, #1b263b, #415a77);");

        try {
            System.out.println("Attempting to find image resource...");
            var url = getClass().getResource("/images/space_background.jpg");
            System.out.println("Image URL: " + url);

            if (url != null) {
                setStyle("-fx-background-image: url('" + url.toExternalForm() + "'); " +
                        "-fx-background-size: cover;");
                System.out.println("Image found and applied");
            } else {
                System.out.println("Image not found, using gradient background");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading background: " + e.getMessage());
        }
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add(AppTheme.STYLE_MENU_BUTTON);
        return button;
    }
}