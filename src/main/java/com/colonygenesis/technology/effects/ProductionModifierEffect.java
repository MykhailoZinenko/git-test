package com.colonygenesis.technology.effects;

import com.colonygenesis.building.BuildingType;
import com.colonygenesis.core.Game;
import com.colonygenesis.resource.ResourceType;
import com.colonygenesis.technology.TechEffect;
import com.colonygenesis.technology.TechEffectType;

import java.io.Serial;

/**
 * Effect that modifies production of a resource type.
 */
public class ProductionModifierEffect implements TechEffect {
    @Serial
    private static final long serialVersionUID = 1L;

    private final ResourceType resourceType;
    private final double modifier;
    private final BuildingType buildingType;  // null means all buildings

    public ProductionModifierEffect(ResourceType resourceType, double modifier, BuildingType buildingType) {
        this.resourceType = resourceType;
        this.modifier = modifier;
        this.buildingType = buildingType;
    }

    @Override
    public void apply(Game game) {
        game.getTechManager().addProductionModifier(resourceType, modifier, buildingType);
    }

    @Override
    public String getDescription() {
        String target = buildingType != null ?
                buildingType.getName() + " buildings" : "all production buildings";
        return String.format("%+.0f%% %s production from %s",
                (modifier - 1.0) * 100, resourceType.getName(), target);
    }

    @Override
    public TechEffectType getType() {
        return TechEffectType.PRODUCTION_MODIFIER;
    }
}
