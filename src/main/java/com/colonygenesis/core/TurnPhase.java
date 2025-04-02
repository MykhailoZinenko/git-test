package com.colonygenesis.core;

import java.io.Serializable;

/**
 * Enumeration of turn phases in the game.
 * Each phase represents a different stage of gameplay within a turn.
 */
public enum TurnPhase implements Serializable {
    /**
     * Planning phase where players make decisions and set priorities.
     */
    PLANNING("Planning", "Plan your next actions", true),

    /**
     * Building phase where construction and upgrades are processed.
     */
    BUILDING("Building", "Construct and upgrade buildings", false),

    /**
     * Production phase where resources are produced and consumed.
     */
    PRODUCTION("Production", "Resource production and consumption", false),

    /**
     * Events phase where random events and crises are handled.
     */
    EVENTS("Events", "Handle planetary events", false),

    /**
     * End turn phase that finalizes the current turn.
     */
    END_TURN("End Turn", "Finalize turn and advance", false);

    private final String name;
    private final String description;
    private final boolean requiresInput;

    /**
     * Constructs a turn phase with specified properties.
     *
     * @param name The name of the phase
     * @param description The description of the phase
     * @param requiresInput Whether this phase requires player input
     */
    TurnPhase(String name, String description, boolean requiresInput) {
        this.name = name;
        this.description = description;
        this.requiresInput = requiresInput;
    }

    /**
     * Gets the name of the phase.
     *
     * @return The phase name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the phase.
     *
     * @return The phase description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Checks if this phase requires player input.
     *
     * @return true if player input is required, false otherwise
     */
    public boolean requiresInput() {
        return requiresInput;
    }
}