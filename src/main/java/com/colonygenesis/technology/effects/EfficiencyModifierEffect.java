package com.colonygenesis.technology.effects;

import com.colonygenesis.core.Game;
import com.colonygenesis.technology.TechEffect;
import com.colonygenesis.technology.TechEffectType;

import java.io.Serial;

/**
 * Effect that modifies building efficiency without workers.
 */
public class EfficiencyModifierEffect implements TechEffect {
    @Serial
    private static final long serialVersionUID = 1L;

    private final double baseEfficiency;

    public EfficiencyModifierEffect(double baseEfficiency) {
        this.baseEfficiency = baseEfficiency;
    }

    @Override
    public void apply(Game game) {
        game.getTechManager().setBaseEfficiency(baseEfficiency);
    }

    @Override
    public String getDescription() {
        return String.format("Buildings operate at %.0f%% efficiency without workers",
                baseEfficiency * 100);
    }

    @Override
    public TechEffectType getType() {
        return TechEffectType.EFFICIENCY_MODIFIER;
    }
}