package com.colonygenesis.ui;

import com.colonygenesis.core.GameState;
import com.colonygenesis.resource.ResourceType;
import com.colonygenesis.ui.components.ActionButton;
import com.colonygenesis.ui.styling.AppTheme;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 * Screen displayed when the game ends due to resource depletion or other failures.
 */
public class GameOverScreen extends BorderPane implements IScreenController {
    private String reason;
    private String description;
    private Label titleLabel;
    private Label reasonLabel;
    private Label descriptionLabel;
    private Label statsLabel;

    /**
     * Constructs a new game over screen.
     */
    public GameOverScreen() {
        getStyleClass().add(AppTheme.STYLE_MENU_SCREEN);
        initializeUI();
    }

    /**
     * Initializes the UI components.
     */
    private void initializeUI() {
        VBox container = new VBox(30);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(50));
        container.setMaxWidth(800);

        // Game Over title
        titleLabel = new Label("GAME OVER");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 60));
        titleLabel.setTextFill(Color.RED);

        // Add glow effect
        DropShadow glow = new DropShadow();
        glow.setColor(Color.RED);
        glow.setRadius(20);
        glow.setSpread(0.3);
        titleLabel.setEffect(glow);

        // Reason
        reasonLabel = new Label();
        reasonLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        reasonLabel.setTextFill(Color.WHITE);
        reasonLabel.setWrapText(true);

        // Description
        descriptionLabel = new Label();
        descriptionLabel.setFont(Font.font("System", 18));
        descriptionLabel.setTextFill(Color.LIGHTGRAY);
        descriptionLabel.setWrapText(true);

        // Game stats
        statsLabel = new Label();
        statsLabel.setFont(Font.font("System", 18));
        statsLabel.setTextFill(Color.LIGHTGRAY);
        statsLabel.setWrapText(true);

        // Buttons
        ActionButton mainMenuButton = new ActionButton("Return to Main Menu", ActionButton.ButtonType.PRIMARY);
        mainMenuButton.setOnAction(e -> ScreenManager.getInstance().activateScreen(GameState.MAIN_MENU));

        ActionButton loadGameButton = new ActionButton("Load Game", ActionButton.ButtonType.SUCCESS);
        loadGameButton.setOnAction(e -> ScreenManager.getInstance().activateScreen(GameState.LOAD_GAME));

        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(loadGameButton, mainMenuButton);

        container.getChildren().addAll(titleLabel, reasonLabel, descriptionLabel, statsLabel, buttonBox);
        setCenter(container);

        // Add animation
        setupAnimations();
    }

    /**
     * Sets up game over screen animations.
     */
    private void setupAnimations() {
        // Title pulsing animation for attention
        Timeline pulseAnimation = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    DropShadow glow = (DropShadow) titleLabel.getEffect();
                    glow.setRadius(20);
                }),
                new KeyFrame(Duration.seconds(1), e -> {
                    DropShadow glow = (DropShadow) titleLabel.getEffect();
                    glow.setRadius(30);
                })
        );
        pulseAnimation.setCycleCount(Timeline.INDEFINITE);
        pulseAnimation.setAutoReverse(true);
        pulseAnimation.play();

        // Fade in animation
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), this);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    /**
     * Sets the game over reason and description.
     */
    public void setGameOverDetails(String reason, String description) {
        this.reason = reason;
        this.description = description;

        reasonLabel.setText(reason);
        descriptionLabel.setText(description);

        // Get stats from current game if available
        if (ScreenManager.getInstance().getCurrentGame() != null) {
            var game = ScreenManager.getInstance().getCurrentGame();
            String stats = String.format(
                    "Colony: %s\n" +
                            "Survived for: %d turns\n" +
                            "Final Population: %d\n" +
                            "Buildings Constructed: %d",
                    game.getColonyName(),
                    game.getCurrentTurn(),
                    game.getResourceManager().getResource(ResourceType.POPULATION),
                    game.getBuildingManager().getBuildingCount()
            );
            statsLabel.setText(stats);
        }
    }

    @Override
    public Parent getRoot() {
        return this;
    }

    @Override
    public void initialize() {
        // Nothing to initialize
    }

    @Override
    public void onShow() {
        // Start animations
        setupAnimations();
    }

    @Override
    public void onHide() {
        // Stop animations if needed
    }

    @Override
    public void update() {
        // Nothing to update
    }
}