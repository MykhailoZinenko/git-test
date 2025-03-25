package com.colonygenesis.core;

import com.colonygenesis.util.LoggerUtil;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Manages the turn-based progression of the game.
 * Handles turn phases and transitions.
 */
public class TurnManager implements Serializable {
    private static final Logger LOGGER = LoggerUtil.getLogger(TurnManager.class);
    private static final long serialVersionUID = 1L;

    private final Game game;
    private int turnNumber;
    private TurnPhase currentPhase;
    private boolean phaseCompleted;

    /**
     * Constructs a turn manager for the specified game.
     *
     * @param game The game
     */
    public TurnManager(Game game) {
        this.game = game;
        this.turnNumber = 1;
        this.currentPhase = TurnPhase.PLANNING;
        this.phaseCompleted = false;

        LOGGER.info("TurnManager initialized at turn 1, phase: PLANNING");
    }

    /**
     * Advances to the next turn.
     * Resets the phase to PLANNING.
     */
    public void advanceTurn() {
        int previousTurn = turnNumber;
        turnNumber++;
        currentPhase = TurnPhase.PLANNING;
        phaseCompleted = false;

        LOGGER.info("Starting turn " + turnNumber);
        game.setCurrentTurn(turnNumber);
    }

    /**
     * Advances to the next phase in the current turn.
     * If the current phase requires input and is not completed, the advancement is blocked.
     */
    public void advancePhase() {
        if (currentPhase.requiresInput() && !phaseCompleted) {
            LOGGER.warning("Attempting to advance from " + currentPhase.getName() + " which was not completed");
            // Allow advancement in development for testing
        }

        int ordinal = currentPhase.ordinal();
        TurnPhase previousPhase = currentPhase;

        TurnPhase[] phases = TurnPhase.values();
        currentPhase = phases[(ordinal + 1) % phases.length];
        phaseCompleted = false;

        LOGGER.info("Phase changed to: " + currentPhase.getName());

        if (!currentPhase.requiresInput()) {
            executeCurrentPhase();
        }
    }

    /**
     * Executes the current phase.
     * Applies the effects of the current phase to the game state.
     */
    public void executeCurrentPhase() {
        LOGGER.info("Executing phase: " + currentPhase.getName());

        switch (currentPhase) {
            case PLANNING:
                // Planning phase is for player decisions
                // Nothing to execute automatically
                break;

            case BUILDING:
                // Process construction progress
                LOGGER.info("Processing building construction");
                break;

            case PRODUCTION:
                // Execute production phase logic
                LOGGER.info("Processing resource production/consumption");
                game.getResourceManager().processTurn();
                break;

            case EVENTS:
                // Execute events phase logic
                LOGGER.info("Processing events");
                break;

            case END_TURN:
                // Execute end turn logic
                LOGGER.info("Ending turn");
                advanceTurn();
                break;
        }

        phaseCompleted = true;

        if (!currentPhase.requiresInput() && currentPhase != TurnPhase.END_TURN) {
            advancePhase();
        }
    }

    /**
     * Gets the current phase.
     *
     * @return The current phase
     */
    public TurnPhase getCurrentPhase() {
        return currentPhase;
    }

    /**
     * Gets the current turn number.
     *
     * @return The current turn number
     */
    public int getTurnNumber() {
        return turnNumber;
    }

    /**
     * Checks if the current phase is completed.
     *
     * @return true if completed, false otherwise
     */
    public boolean isPhaseCompleted() {
        return phaseCompleted;
    }

    /**
     * Sets the completion status of the current phase.
     *
     * @param completed The completion status
     */
    public void setPhaseCompleted(boolean completed) {
        this.phaseCompleted = completed;
    }
}