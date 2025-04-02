package com.colonygenesis.core;

import com.colonygenesis.ui.events.EventBus;
import com.colonygenesis.ui.events.TurnEvents;
import com.colonygenesis.util.LoggerUtil;
import com.colonygenesis.util.Result;

import java.io.Serial;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Manages the turn-based progression of the game.
 * Handles turn phases and transitions.
 */
public class TurnManager implements Serializable {
    private static final Logger LOGGER = LoggerUtil.getLogger(TurnManager.class);
    @Serial
    private static final long serialVersionUID = 1L;

    private final Game game;
    private int turnNumber;
    private TurnPhase currentPhase;
    private boolean phaseCompleted;

    // Event bus for publishing events
    private final transient EventBus eventBus;

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
        this.eventBus = EventBus.getInstance();

        LOGGER.info("TurnManager initialized at turn 1, phase: PLANNING");
    }

    /**
     * Advances to the next turn.
     * Resets the phase to PLANNING.
     */
    public void advanceTurn() {
        int previousTurn = turnNumber;
        TurnPhase previousPhase = currentPhase;

        turnNumber++;
        currentPhase = TurnPhase.PLANNING;
        phaseCompleted = false;

        LOGGER.info("Starting turn " + turnNumber);
        game.setCurrentTurn(turnNumber);

        // Publish turn advanced event
        eventBus.publish(new TurnEvents.TurnAdvancedEvent(turnNumber, previousTurn));

        // Publish phase changed event
        eventBus.publish(new TurnEvents.PhaseChangedEvent(currentPhase, previousPhase, turnNumber));
    }

    /**
     * Advances to the next phase in the current turn.
     * If the current phase requires input and is not completed, the advancement is blocked.
     *
     * @return A Result containing the new phase if successful, or an error message if failed
     */
    public Result<TurnPhase> advancePhase() {
        if (currentPhase.requiresInput() && !phaseCompleted) {
            LOGGER.warning("Attempting to advance from " + currentPhase.getName() + " which was not completed");
            return Result.failure("Phase " + currentPhase.getName() + " is not completed");
        }

        int ordinal = currentPhase.ordinal();
        TurnPhase previousPhase = currentPhase;

        TurnPhase[] phases = TurnPhase.values();
        currentPhase = phases[(ordinal + 1) % phases.length];
        phaseCompleted = false;

        LOGGER.info("Phase changed to: " + currentPhase.getName());

        // Publish phase changed event
        eventBus.publish(new TurnEvents.PhaseChangedEvent(currentPhase, previousPhase, turnNumber));

        if (currentPhase == TurnPhase.END_TURN) {
            // If this is the end turn phase, automatically advance to the next turn
            advanceTurn();
        } else if (!currentPhase.requiresInput()) {
            // If the new phase doesn't require input, execute it automatically
            executeCurrentPhase();
        }

        return Result.success(currentPhase);
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
                game.getBuildingManager().processTurn();
                break;

            case PRODUCTION:
                // Execute production phase logic
                LOGGER.info("Processing resource production/consumption");
                game.getResourceManager().processTurn();

                // Process colony population growth
                game.getColonyManager().processTurn();
                break;

            case EVENTS:
                // Execute events phase logic
                LOGGER.info("Processing events");
                break;

            case END_TURN:
                // End turn is handled in advancePhase
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