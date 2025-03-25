package com.colonygenesis.ui.components;

import com.colonygenesis.ui.styling.AppTheme;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;

/**
 * Specialized button for game actions with consistent styling.
 */
public class ActionButton extends Button {

    /**
     * Button type enum for different action types.
     */
    public enum ButtonType {
        PRIMARY,
        SUCCESS,
        WARNING,
        DANGER,
        MENU
    }

    /**
     * Creates a new action button with default styling.
     *
     * @param text The button text
     */
    public ActionButton(String text) {
        this(text, ButtonType.PRIMARY, null);
    }

    /**
     * Creates a new action button with specified type.
     *
     * @param text The button text
     * @param type The button type
     */
    public ActionButton(String text, ButtonType type) {
        this(text, type, null);
    }

    /**
     * Creates a new action button with tooltip.
     *
     * @param text The button text
     * @param tooltipText The tooltip text
     */
    public ActionButton(String text, String tooltipText) {
        this(text, ButtonType.PRIMARY, tooltipText);
    }

    /**
     * Creates a new action button with type and tooltip.
     *
     * @param text The button text
     * @param type The button type
     * @param tooltipText The tooltip text
     */
    public ActionButton(String text, ButtonType type, String tooltipText) {
        super(text);
        getStyleClass().add(AppTheme.STYLE_BUTTON);

        switch (type) {
            case PRIMARY:
                getStyleClass().add(AppTheme.STYLE_BUTTON_PRIMARY);
                break;
            case SUCCESS:
                getStyleClass().add(AppTheme.STYLE_BUTTON_SUCCESS);
                break;
            case WARNING:
                getStyleClass().add(AppTheme.STYLE_BUTTON_WARNING);
                break;
            case DANGER:
                getStyleClass().add(AppTheme.STYLE_BUTTON_DANGER);
                break;
            case MENU:
                getStyleClass().add(AppTheme.STYLE_BUTTON_MENU);
                break;
        }

        if (tooltipText != null && !tooltipText.isEmpty()) {
            Tooltip tooltip = new Tooltip(tooltipText);
            setTooltip(tooltip);
        }
    }

    /**
     * Sets the button size.
     *
     * @param small Whether this is a small button
     */
    public void setSmall(boolean small) {
        if (small) {
            getStyleClass().add(AppTheme.STYLE_BUTTON_SMALL);
        } else {
            getStyleClass().remove(AppTheme.STYLE_BUTTON_SMALL);
        }
    }

    /**
     * Sets the button as a large button.
     *
     * @param large Whether this is a large button
     */
    public void setLarge(boolean large) {
        if (large) {
            getStyleClass().add(AppTheme.STYLE_BUTTON_LARGE);
        } else {
            getStyleClass().remove(AppTheme.STYLE_BUTTON_LARGE);
        }
    }
}