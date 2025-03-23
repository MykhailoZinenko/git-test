package com.colonygenesis.ui;

import javafx.scene.Parent;

/**
 * Interface for screen controllers in the application.
 * Defines the lifecycle methods for screens.
 */
public interface IScreenController {

    /**
     * Gets the root JavaFX node for this screen.
     *
     * @return The root node
     */
    Parent getRoot();

    /**
     * Initializes the screen.
     * Called once when the screen is first registered.
     */
    void initialize();

    /**
     * Called when the screen is shown.
     * Use this method to refresh dynamic content.
     */
    void onShow();

    /**
     * Called when the screen is hidden.
     * Use this method to clean up resources if needed.
     */
    void onHide();

    /**
     * Updates the screen content.
     * Called periodically or when the game state changes.
     */
    void update();
}