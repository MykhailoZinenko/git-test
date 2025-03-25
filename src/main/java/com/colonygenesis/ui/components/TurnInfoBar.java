package com.colonygenesis.ui.components;

import com.colonygenesis.core.TurnPhase;
import com.colonygenesis.ui.styling.AppTheme;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Component for displaying turn and phase information in the game UI.
 */
public class TurnInfoBar extends HBox {
    private final Label turnLabel;
    private final Label phaseLabel;
    private final ActionButton menuButton;

    /**
     * Creates a new turn info bar.
     *
     * @param onMenuClicked Callback for when the menu button is clicked
     */
    public TurnInfoBar(Runnable onMenuClicked) {
        setSpacing(20);

        VBox turnPhaseInfo = new VBox();
        turnPhaseInfo.getStyleClass().add(AppTheme.STYLE_TURN_INFO);

        turnLabel = new Label("Turn: 1");
        turnLabel.getStyleClass().add(AppTheme.STYLE_TURN_LABEL);

        phaseLabel = new Label("Phase: Planning");
        phaseLabel.getStyleClass().add(AppTheme.STYLE_PHASE_LABEL);

        turnPhaseInfo.getChildren().addAll(turnLabel, phaseLabel);

        menuButton = new ActionButton("Menu", ActionButton.ButtonType.PRIMARY, "Open game menu");
        menuButton.setPrefWidth(100);
        menuButton.setOnAction(e -> {
            if (onMenuClicked != null) {
                onMenuClicked.run();
            }
        });

        getChildren().addAll(turnPhaseInfo, menuButton);
    }

    /**
     * Updates the turn and phase display.
     *
     * @param turn The current turn number
     * @param phase The current game phase
     */
    public void update(int turn, TurnPhase phase) {
        turnLabel.setText("Turn: " + turn);
        phaseLabel.setText("Phase: " + phase.getName());
    }
}