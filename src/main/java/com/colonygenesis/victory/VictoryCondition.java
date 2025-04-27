package com.colonygenesis.victory;

import com.colonygenesis.core.Game;

/**
 * Interface for all victory conditions.
 */
public interface VictoryCondition {
    /**
     * Checks the progress towards this victory condition.
     * @return true if the condition has been met
     */
    boolean checkProgress(Game game);

    /**
     * Checks if this victory condition has been achieved.
     * This takes into account both progress and unlock status.
     * @return true if the victory has been achieved
     */
    boolean isAchieved(Game game);

    /**
     * Gets a description of this victory condition.
     * @return The description
     */
    String getDescription();

    /**
     * Gets the type of this victory condition.
     * @return The victory type
     */
    VictoryType getType();
}