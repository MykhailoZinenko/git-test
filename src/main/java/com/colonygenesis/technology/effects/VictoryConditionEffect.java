package com.colonygenesis.technology.effects;

import com.colonygenesis.core.Game;
import com.colonygenesis.technology.TechEffect;
import com.colonygenesis.technology.TechEffectType;

import com.colonygenesis.victory.VictoryType;
import com.colonygenesis.util.LoggerUtil;
import java.util.logging.Logger;

import java.io.Serial;

/**
 * Effect that enables a victory condition.
 */
public class VictoryConditionEffect implements TechEffect {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerUtil.getLogger(VictoryConditionEffect.class);

    private final String victoryType;

    public VictoryConditionEffect(String victoryType) {
        this.victoryType = victoryType;
    }

    @Override
    public void apply(Game game) {
        // Unlock the victory condition
        switch (victoryType.toLowerCase()) {
            case "scientific victory":
                game.getVictoryManager().unlockVictoryCondition(VictoryType.SCIENTIFIC);
                break;
            case "industrial victory":
                game.getVictoryManager().unlockVictoryCondition(VictoryType.INDUSTRIAL);
                break;
            case "harmony victory":
                game.getVictoryManager().unlockVictoryCondition(VictoryType.HARMONY);
                break;
        }
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