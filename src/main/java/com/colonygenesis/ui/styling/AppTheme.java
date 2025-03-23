package com.colonygenesis.ui.styling;

import org.kordamp.bootstrapfx.BootstrapFX;

/**
 * Provides centralized styling constants for the application.
 * Contains color definitions, style classes, and dimensions.
 */
public class AppTheme {
    /**
     * Path to the main stylesheet.
     */
    public static final String MAIN_STYLESHEET = AppTheme.class.getResource("/styles/main.css").toExternalForm();

    /**
     * Path to the Bootstrap stylesheet.
     */
    public static final String BOOTSTRAP_STYLESHEET = BootstrapFX.bootstrapFXStylesheet();

    /**
     * Primary color for UI elements.
     */
    public static final String COLOR_PRIMARY = "#2E5077";

    /**
     * Secondary color for UI elements.
     */
    public static final String COLOR_SECONDARY = "#4B7399";

    /**
     * Success color for positive actions and statuses.
     */
    public static final String COLOR_SUCCESS = "#28a745";

    /**
     * Warning color for alert actions and statuses.
     */
    public static final String COLOR_WARNING = "#ffc107";

    /**
     * Danger color for destructive actions and errors.
     */
    public static final String COLOR_DANGER = "#dc3545";

    /**
     * Info color for informational messages.
     */
    public static final String COLOR_INFO = "#17a2b8";

    /**
     * Background color for the application.
     */
    public static final String COLOR_BACKGROUND = "#121212";

    /**
     * Surface color for UI components.
     */
    public static final String COLOR_SURFACE = "#1E1E1E";

    /**
     * Primary text color.
     */
    public static final String COLOR_TEXT_PRIMARY = "#E0E0E0";

    /**
     * Secondary text color.
     */
    public static final String COLOR_TEXT_SECONDARY = "#A0A0A0";

    /**
     * Style class for the application title.
     */
    public static final String STYLE_TITLE = "app-title";

    /**
     * Style class for menu buttons.
     */
    public static final String STYLE_MENU_BUTTON = "menu-button";

    /**
     * Style class for panels.
     */
    public static final String STYLE_PANEL = "game-panel";

    /**
     * Style class for panel headers.
     */
    public static final String STYLE_PANEL_HEADER = "panel-header";

    /**
     * Style class for hexagonal tiles.
     */
    public static final String STYLE_HEX_TILE = "hex-tile";

    /**
     * Font size for titles.
     */
    public static final double FONT_SIZE_TITLE = 36;

    /**
     * Font size for subtitles.
     */
    public static final double FONT_SIZE_SUBTITLE = 24;

    /**
     * Font size for regular text.
     */
    public static final double FONT_SIZE_REGULAR = 14;

    /**
     * Standard padding size.
     */
    public static final double PADDING_REGULAR = 15;

    /**
     * Standard button width.
     */
    public static final double BUTTON_WIDTH = 220;

    /**
     * Main font family for the application.
     */
    public static final String FONT_FAMILY_MAIN = "System";

    // Private constructor to prevent instantiation
    private AppTheme() {}
}