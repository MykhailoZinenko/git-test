package com.colonygenesis.technology.effects;

import com.colonygenesis.building.BuildingType;
import com.colonygenesis.core.Game;
import com.colonygenesis.technology.TechEffect;
import com.colonygenesis.technology.TechEffectType;

import java.io.Serial;

/**
 * Effect that modifies construction time.
 */
public class ConstructionTimeModifierEffect implements TechEffect {
    @Serial
    private static final long serialVersionUID = 1L;

    private final double modifier;
    private final BuildingType buildingType;  // null means all buildings

    public ConstructionTimeModifierEffect(double modifier, BuildingType buildingType) {
        this.modifier = modifier;
        this.buildingType = buildingType;
    }

    @Override
    public void apply(Game game) {
        game.getTechManager().addConstructionTimeModifier(modifier, buildingType);
    }

    @Override
    public String getDescription() {
        String target = buildingType != null ?
                buildingType.getName() + " buildings" : "all buildings";
        return String.format("%+.0f%% construction time for %s",
                (modifier - 1.0) * 100, target);
    }

    @Override
    public TechEffectType getType() {
        return TechEffectType.CONSTRUCTION_TIME_MODIFIER;
    }
}