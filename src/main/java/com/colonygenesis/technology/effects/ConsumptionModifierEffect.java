package com.colonygenesis.technology.effects;

import com.colonygenesis.building.BuildingType;
import com.colonygenesis.core.Game;
import com.colonygenesis.resource.ResourceType;
import com.colonygenesis.technology.TechEffect;
import com.colonygenesis.technology.TechEffectType;

import java.io.Serial;

/**
 * Effect that modifies resource consumption.
 */
public class ConsumptionModifierEffect implements TechEffect {
    @Serial
    private static final long serialVersionUID = 1L;

    private final ResourceType resourceType;
    private final double modifier;
    private final BuildingType buildingType;  // null means all buildings

    public ConsumptionModifierEffect(ResourceType resourceType, double modifier, BuildingType buildingType) {
        this.resourceType = resourceType;
        this.modifier = modifier;
        this.buildingType = buildingType;
    }

    @Override
    public void apply(Game game) {
        game.getTechManager().addConsumptionModifier(resourceType, modifier, buildingType);
    }

    @Override
    public String getDescription() {
        String target = buildingType != null ?
                buildingType.getName() + " buildings" : "all buildings";
        return String.format("%+.0f%% %s consumption for %s",
                (modifier - 1.0) * 100, resourceType.getName(), target);
    }

    @Override
    public TechEffectType getType() {
        return TechEffectType.CONSUMPTION_MODIFIER;
    }
}