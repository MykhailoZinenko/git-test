package com.colonygenesis.ui;

import com.colonygenesis.core.GameState;
import com.colonygenesis.ui.styling.AppTheme;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class MainMenuScreen extends BorderPane implements IScreenController {

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

        newGameBtn.setOnAction(e -> handleNewGameRequest());
        loadGameBtn.setOnAction(e -> handleLoadGameRequest());
        settingsBtn.setOnAction(e -> handleSettingsRequest());
        exitBtn.setOnAction(e -> handleExitRequest());

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

    private void handleNewGameRequest() {
        System.out.println("New Game requested");
        ScreenManager.getInstance().activateScreen(GameState.GAME_SETUP);
    }

    private void handleLoadGameRequest() {
        System.out.println("Load Game requested");
    }

    private void handleSettingsRequest() {
        System.out.println("Settings requested");
    }

    private void handleExitRequest() {
        System.exit(0);
    }

    @Override
    public Parent getRoot() {
        return this;
    }

    @Override
    public void initialize() {}

    @Override
    public void onShow() {}

    @Override
    public void onHide() {}

    @Override
    public void update() {}
}