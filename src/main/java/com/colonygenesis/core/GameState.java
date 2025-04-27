package com.colonygenesis.core;

/**
 * Enumeration of possible game states.
 * Used for screen navigation and game flow control.
 */
public enum GameState {
    /**
     * Main menu screen, the entry point of the application.
     */
    MAIN_MENU,

    /**
     * Game setup screen for configuring a new game.
     */
    GAME_SETUP,

    /**
     * Load game screen for loading saved games.
     */
    LOAD_GAME,

    /**
     * Main gameplay screen.
     */
    GAMEPLAY,

    /**
     * Settings screen.
     */
    SETTINGS,

    /**
     * Pause menu screen during gameplay.
     */
    PAUSE_MENU,

    /**
     * Game over screen.
     */
    GAME_OVER,

    /**
     * Victory screen.
     */
    VICTORY,
}