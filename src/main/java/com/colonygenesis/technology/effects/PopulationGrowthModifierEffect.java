package com.colonygenesis.technology.effects;

import com.colonygenesis.core.Game;
import com.colonygenesis.technology.TechEffect;
import com.colonygenesis.technology.TechEffectType;

import java.io.Serial;

/**
 * Effect that modifies population growth rate.
 */
public class PopulationGrowthModifierEffect implements TechEffect {
    @Serial
    private static final long serialVersionUID = 1L;

    private final double modifier;

    public PopulationGrowthModifierEffect(double modifier) {
        this.modifier = modifier;
    }

    @Override
    public void apply(Game game) {
        game.getTechManager().addPopulationGrowthModifier(modifier);
    }

    @Override
    public String getDescription() {
        return String.format("%+.0f%% population growth rate", (modifier - 1.0) * 100);
    }

    @Override
    public TechEffectType getType() {
        return TechEffectType.POPULATION_GROWTH_MODIFIER;
    }
}