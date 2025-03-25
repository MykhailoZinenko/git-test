package com.colonygenesis.ui.components;

import com.colonygenesis.ui.styling.AppTheme;
import javafx.scene.layout.HBox;

/**
 * Component for displaying game control buttons in the footer.
 */
public class GameControlBar extends HBox {
    private final ActionButton nextPhaseButton;

    /**
     * Creates a new game control bar.
     *
     * @param onNextPhase Callback for when the next phase button is clicked
     */
    public GameControlBar(Runnable onNextPhase) {
        getStyleClass().add(AppTheme.STYLE_FOOTER);

        nextPhaseButton = new ActionButton(
                "Next Phase",
                ActionButton.ButtonType.SUCCESS,
                "Advance to the next game phase"
        );
        nextPhaseButton.setPrefWidth(150);
        nextPhaseButton.setOnAction(e -> {
            if (onNextPhase != null) {
                onNextPhase.run();
            }
        });

        getChildren().add(nextPhaseButton);
    }

    /**
     * Enables or disables the next phase button.
     *
     * @param enabled Whether the button should be enabled
     */
    public void setNextPhaseEnabled(boolean enabled) {
        nextPhaseButton.setDisable(!enabled);
    }
}