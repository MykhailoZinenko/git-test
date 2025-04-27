package com.colonygenesis.technology.effects;

import com.colonygenesis.building.BuildingType;
import com.colonygenesis.core.Game;
import com.colonygenesis.technology.TechEffect;
import com.colonygenesis.technology.TechEffectType;

import java.io.Serial;

/**
 * Effect that unlocks a new building type.
 */
public class BuildingUnlockEffect implements TechEffect {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String buildingId;
    private final String buildingName;
    private final BuildingType buildingType;

    public BuildingUnlockEffect(String buildingId, String buildingName, BuildingType buildingType) {
        this.buildingId = buildingId;
        this.buildingName = buildingName;
        this.buildingType = buildingType;
    }

    @Override
    public void apply(Game game) {
        // For now, we'll store this in TechManager
        // In practice, you'd want to extend BuildingManager to handle building unlocks
        game.getTechManager().addUnlockedBuilding(buildingId);
    }

    @Override
    public String getDescription() {
        return "Unlocks " + buildingName;
    }

    @Override
    public TechEffectType getType() {
        return TechEffectType.BUILDING_UNLOCK;
    }

    public String getBuildingId() {
        return buildingId;
    }
}