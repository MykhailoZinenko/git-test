package com.colonygenesis.technology.effects;

import com.colonygenesis.building.BuildingType;
import com.colonygenesis.core.Game;
import com.colonygenesis.technology.TechEffect;
import com.colonygenesis.technology.TechEffectType;

import java.io.Serial;

/**
 * Effect that modifies worker requirements for buildings.
 */
public class WorkerModifierEffect implements TechEffect {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int reduction;
    private final BuildingType buildingType;  // null means all buildings

    public WorkerModifierEffect(int reduction, BuildingType buildingType) {
        this.reduction = reduction;
        this.buildingType = buildingType;
    }

    @Override
    public void apply(Game game) {
        game.getTechManager().addWorkerReduction(reduction, buildingType);
    }

    @Override
    public String getDescription() {
        String target = buildingType != null ?
                buildingType.getName() + " buildings" : "all buildings";
        return String.format("-%d worker requirement for %s", reduction, target);
    }

    @Override
    public TechEffectType getType() {
        return TechEffectType.WORKER_MODIFIER;
    }
}