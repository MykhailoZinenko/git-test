package com.colonygenesis.technology;

import com.colonygenesis.core.Game;
import java.io.Serializable;

/**
 * Interface for technology effects that can be applied when a technology is researched.
 */
public interface TechEffect extends Serializable {
    /**
     * Applies the effect of this technology to the game.
     *
     * @param game The game instance
     */
    void apply(Game game);

    /**
     * Gets a description of this effect.
     *
     * @return The effect description
     */
    String getDescription();

    /**
     * Gets the type of this effect.
     *
     * @return The effect type
     */
    TechEffectType getType();
}