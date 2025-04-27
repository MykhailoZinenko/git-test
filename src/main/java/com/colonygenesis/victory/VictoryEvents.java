package com.colonygenesis.victory;

import com.colonygenesis.ui.events.GameEvent;

/**
 * Events related to victory conditions.
 */
public class VictoryEvents {

    /**
     * Event fired when a victory condition is achieved.
     */
    public static class VictoryAchievedEvent implements GameEvent {
        private final VictoryType victoryType;

        public VictoryAchievedEvent(VictoryType victoryType) {
            this.victoryType = victoryType;
            /**
             * Event fired when the game is over due to resource depletion or other failure conditions.
             */
        }

        public VictoryType getVictoryType() {
            return victoryType;
        }

        @Override
        public String getName() {
            return "VictoryAchieved";
        }
    }

    /**
     * Event fired when victory progress is updated.
     */
    public static class VictoryProgressUpdatedEvent implements GameEvent {
        private final VictoryType victoryType;
        private final float progress;

        public VictoryProgressUpdatedEvent(VictoryType victoryType, float progress) {
            this.victoryType = victoryType;
            this.progress = progress;
        }

        public VictoryType getVictoryType() {
            return victoryType;
        }

        public float getProgress() {
            return progress;
        }

        @Override
        public String getName() {
            return "VictoryProgressUpdated";
        }
    }

    /**
     * Event fired when a victory condition is unlocked.
     */
    public static class VictoryUnlockedEvent implements GameEvent {
        private final VictoryType victoryType;

        public VictoryUnlockedEvent(VictoryType victoryType) {
            this.victoryType = victoryType;
        }

        public VictoryType getVictoryType() {
            return victoryType;
        }

        @Override
        public String getName() {
            return "VictoryUnlocked";
        }
    }

    public static class GameOverEvent implements GameEvent {
        private final String reason;
        private final String description;

        public GameOverEvent(String reason, String description) {
            this.reason = reason;
            this.description = description;
        }

        public String getReason() {
            return reason;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String getName() {
            return "GameOver";
        }
    }
}