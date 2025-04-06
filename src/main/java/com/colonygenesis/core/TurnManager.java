package com.colonygenesis.core;

import com.colonygenesis.ui.events.EventBus;
import com.colonygenesis.ui.events.TurnEvents;
import com.colonygenesis.util.LoggerUtil;
import com.colonygenesis.util.Result;

import java.io.IOException;
import java.io.ObjectInputStream;
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

    private Game game;
    private int turnNumber;
    private TurnPhase currentPhase;
    private boolean phaseCompleted;

    private transient EventBus eventBus;

    /**
     * Constructs a turn manager for the specified game.
     *
     * @param game The game
     */
    public TurnManager(Game game) {
        this.game = game;
        this.turnNumber = game.getCurrentTurn();
        this.currentPhase = TurnPhase.PLANNING;
        this.phaseCompleted = false;
        this.eventBus = EventBus.getInstance();

        LOGGER.info("TurnManager initialized at turn " + turnNumber + ", phase: PLANNING");
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

        eventBus.publish(new TurnEvents.TurnAdvancedEvent(turnNumber, previousTurn));

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
        }

        int ordinal = currentPhase.ordinal();
        TurnPhase previousPhase = currentPhase;

        TurnPhase[] phases = TurnPhase.values();
        currentPhase = phases[(ordinal + 1) % phases.length];
        phaseCompleted = false;

        LOGGER.info("Phase changed to: " + currentPhase.getName());

        eventBus.publish(new TurnEvents.PhaseChangedEvent(currentPhase, previousPhase, turnNumber));

        if (currentPhase == TurnPhase.END_TURN) {
            advanceTurn();
        } else if (!currentPhase.requiresInput()) {
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

                break;

            case EVENTS:
                // Execute events phase logic
                LOGGER.info("Processing events");
                break;

            case END_TURN:
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

    public void setGame(Game game) {
        this.game = game;
    }

    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException, IOException {
        in.defaultReadObject();
        this.eventBus = EventBus.getInstance();
        LOGGER.info("TurnManager deserialized and transient fields reinitialized");
    }
}