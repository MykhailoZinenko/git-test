package com.colonygenesis.ui.events;

import com.colonygenesis.core.TurnPhase;

/**
 * Event classes related to turn management.
 */
public class TurnEvents {

    /**
     * Event fired when a turn advances.
     */
    public static class TurnAdvancedEvent implements GameEvent {
        private final int turnNumber;
        private final int previousTurn;

        /**
         * Creates a new turn advanced event.
         *
         * @param turnNumber The new turn number
         * @param previousTurn The previous turn number
         */
        public TurnAdvancedEvent(int turnNumber, int previousTurn) {
            this.turnNumber = turnNumber;
            this.previousTurn = previousTurn;
        }

        /**
         * Gets the turn number.
         *
         * @return The turn number
         */
        public int getTurnNumber() {
            return turnNumber;
        }

        /**
         * Gets the previous turn number.
         *
         * @return The previous turn
         */
        public int getPreviousTurn() {
            return previousTurn;
        }

        @Override
        public String getName() {
            return "TurnAdvanced";
        }
    }

    /**
     * Event fired when the turn phase changes.
     */
    public static class PhaseChangedEvent implements GameEvent {
        private final TurnPhase phase;
        private final TurnPhase previousPhase;
        private final int turnNumber;

        /**
         * Creates a new phase changed event.
         *
         * @param phase The new phase
         * @param previousPhase The previous phase
         * @param turnNumber The current turn number
         */
        public PhaseChangedEvent(TurnPhase phase, TurnPhase previousPhase, int turnNumber) {
            this.phase = phase;
            this.previousPhase = previousPhase;
            this.turnNumber = turnNumber;
        }

        /**
         * Gets the phase.
         *
         * @return The phase
         */
        public TurnPhase getPhase() {
            return phase;
        }

        /**
         * Gets the previous phase.
         *
         * @return The previous phase
         */
        public TurnPhase getPreviousPhase() {
            return previousPhase;
        }

        /**
         * Gets the turn number.
         *
         * @return The turn number
         */
        public int getTurnNumber() {
            return turnNumber;
        }

        @Override
        public String getName() {
            return "PhaseChanged";
        }
    }
}