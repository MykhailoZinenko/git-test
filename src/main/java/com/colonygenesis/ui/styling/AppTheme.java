package com.colonygenesis.ui.styling;

import org.kordamp.bootstrapfx.BootstrapFX;

public class AppTheme {
    public static final String MAIN_STYLESHEET = AppTheme.class.getResource("/styles/main.css").toExternalForm();
    public static final String BOOTSTRAP_STYLESHEET = BootstrapFX.bootstrapFXStylesheet();

    public static final String COLOR_PRIMARY = "#2E5077";
    public static final String COLOR_SECONDARY = "#4B7399";
    public static final String COLOR_SUCCESS = "#28a745";
    public static final String COLOR_WARNING = "#ffc107";
    public static final String COLOR_DANGER = "#dc3545";
    public static final String COLOR_INFO = "#17a2b8";
    public static final String COLOR_BACKGROUND = "#121212";
    public static final String COLOR_SURFACE = "#1E1E1E";
    public static final String COLOR_TEXT_PRIMARY = "#E0E0E0";
    public static final String COLOR_TEXT_SECONDARY = "#A0A0A0";

    public static final String STYLE_TITLE = "app-title";
    public static final String STYLE_MENU_BUTTON = "menu-button";
    public static final String STYLE_PANEL = "game-panel";
    public static final String STYLE_PANEL_HEADER = "panel-header";
    public static final String STYLE_HEX_TILE = "hex-tile";

    public static final double FONT_SIZE_TITLE = 36;
    public static final double FONT_SIZE_SUBTITLE = 24;
    public static final double FONT_SIZE_REGULAR = 14;
    public static final double PADDING_REGULAR = 15;
    public static final double BUTTON_WIDTH = 220;

    public static final String FONT_FAMILY_MAIN = "System";
}