package com.colonygenesis.technology.effects;

import com.colonygenesis.core.Game;
import com.colonygenesis.technology.TechEffect;
import com.colonygenesis.technology.TechEffectType;

import java.io.Serial;

/**
 * Effect that enables a victory condition.
 */
public class VictoryConditionEffect implements TechEffect {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String victoryType;

    public VictoryConditionEffect(String victoryType) {
        this.victoryType = victoryType;
    }

    @Override
    public void apply(Game game) {
        // Implement victory condition logic when victory system is implemented
    }

    @Override
    public String getDescription() {
        return "Unlocks " + victoryType + " victory condition";
    }

    @Override
    public TechEffectType getType() {
        return TechEffectType.VICTORY_CONDITION;
    }
}