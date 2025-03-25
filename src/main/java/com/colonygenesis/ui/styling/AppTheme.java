package com.colonygenesis.ui.styling;

import javafx.scene.paint.Color;
import org.kordamp.bootstrapfx.BootstrapFX;

public class AppTheme {
    public static final String MAIN_STYLESHEET = "styles/main.css";

    public static final String BOOTSTRAP_STYLESHEET = BootstrapFX.bootstrapFXStylesheet();

    // Color palette
    public static final Color COLOR_BACKGROUND_DARK = Color.rgb(16, 20, 36);
    public static final Color COLOR_BACKGROUND_MEDIUM = Color.rgb(28, 35, 64);
    public static final Color COLOR_BACKGROUND_LIGHT = Color.rgb(40, 50, 92);
    public static final Color COLOR_ACCENT_PRIMARY = Color.rgb(75, 115, 153); // Blue accent
    public static final Color COLOR_ACCENT_SECONDARY = Color.rgb(173, 127, 76); // Bronze accent
    public static final Color COLOR_TEXT_PRIMARY = Color.rgb(230, 230, 230);
    public static final Color COLOR_TEXT_SECONDARY = Color.rgb(180, 180, 180);
    public static final Color COLOR_SUCCESS = Color.rgb(76, 175, 80);
    public static final Color COLOR_WARNING = Color.rgb(255, 152, 0);
    public static final Color COLOR_DANGER = Color.rgb(244, 67, 54);

    // Style class names for layout components
    public static final String STYLE_SCREEN = "game-screen";
    public static final String STYLE_HEADER = "game-header";
    public static final String STYLE_FOOTER = "game-footer";
    public static final String STYLE_SIDEBAR = "game-sidebar";
    public static final String STYLE_PANEL = "game-panel";
    public static final String STYLE_PANEL_TITLE = "panel-title";
    public static final String STYLE_PANEL_CONTENT = "panel-content";

    // Style class names for control elements
    public static final String STYLE_BUTTON = "game-button";
    public static final String STYLE_BUTTON_PRIMARY = "button-primary";
    public static final String STYLE_BUTTON_SUCCESS = "button-success";
    public static final String STYLE_BUTTON_WARNING = "button-warning";
    public static final String STYLE_BUTTON_DANGER = "button-danger";
    public static final String STYLE_BUTTON_MENU = "button-menu";
    public static final String STYLE_BUTTON_SMALL = "button-small";
    public static final String STYLE_BUTTON_LARGE = "button-large";

    // Style class names for text elements
    public static final String STYLE_TITLE = "game-title";
    public static final String STYLE_SUBTITLE = "game-subtitle";
    public static final String STYLE_LABEL = "game-label";
    public static final String STYLE_DESCRIPTION = "game-description";

    // Style class names for resource display
    public static final String STYLE_RESOURCE_BAR = "resource-bar";
    public static final String STYLE_RESOURCE_ITEM = "resource-item";
    public static final String STYLE_RESOURCE_ICON = "resource-icon";
    public static final String STYLE_RESOURCE_LABEL = "resource-label";
    public static final String STYLE_RESOURCE_POSITIVE = "resource-positive";
    public static final String STYLE_RESOURCE_NEGATIVE = "resource-negative";
    public static final String STYLE_RESOURCE_NEUTRAL = "resource-neutral";

    // Style class names for turn display
    public static final String STYLE_TURN_INFO = "turn-info";
    public static final String STYLE_TURN_LABEL = "turn-label";
    public static final String STYLE_PHASE_LABEL = "phase-label";

    // Style class names for map view
    public static final String STYLE_MAP_VIEW = "map-view";
    public static final String STYLE_HEX_TILE = "hex-tile";
    public static final String STYLE_HEX_TILE_SELECTED = "hex-tile-selected";
    public static final String STYLE_HEX_TILE_HOVER = "hex-tile-hover";

    // Style class names for various screens
    public static final String STYLE_MENU_SCREEN = "menu-screen";
    public static final String STYLE_MENU_CONTAINER = "menu-container";
    public static final String STYLE_SETUP_SCREEN = "setup-screen";
    public static final String STYLE_LOAD_SCREEN = "load-screen";
    public static final String STYLE_PAUSE_SCREEN = "pause-screen";

    // Style class names for list and table elements
    public static final String STYLE_LIST_VIEW = "game-list-view";
    public static final String STYLE_LIST_CELL = "game-list-cell";
    public static final String STYLE_TABLE_VIEW = "game-table-view";
    public static final String STYLE_TABLE_CELL = "game-table-cell";

    // Style class names for input elements
    public static final String STYLE_TEXT_FIELD = "game-text-field";
    public static final String STYLE_COMBO_BOX = "game-combo-box";
    public static final String STYLE_CHECK_BOX = "game-check-box";

    /**
     * Gets the color string representation for use in CSS.
     *
     * @param color The color to convert
     * @return CSS color string
     */
    public static String toRgbString(Color color) {
        return String.format("rgb(%d, %d, %d)",
                (int)(color.getRed() * 255),
                (int)(color.getGreen() * 255),
                (int)(color.getBlue() * 255));
    }
}