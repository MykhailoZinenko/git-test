package com.colonygenesis.building;

import com.colonygenesis.core.Game;
import com.colonygenesis.map.Tile;
import com.colonygenesis.resource.ResourceType;

import java.io.Serial;
import java.util.Map;

/**
 * Advanced housing structures unlocked by research.
 */
public class UnlockedHousing extends HabitationBuilding {
    @Serial
    private static final long serialVersionUID = 1L;

    private final UnlockedHousingType housingType;

    /**
     * Constructs a new unlocked housing building.
     *
     * @param housingType The type of housing
     * @param location The tile where the building is located
     */
    public UnlockedHousing(UnlockedHousingType housingType, Tile location, Game game) {
        super(
                housingType.getName(),
                housingType.getDescription(),
                location,
                housingType.getConstructionTime(),
                housingType.getWorkersRequired(),
                housingType.getCapacity(),
                housingType.getComfortLevel(),
                housingType.getGrowthRate(),
                game
        );

        this.housingType = housingType;

        constructionCost.putAll(housingType.getConstructionCost());
        maintenanceCost.putAll(housingType.getMaintenanceCost());
    }

    @Override
    protected void initializeConstructionCost() {
        constructionCost.putAll(housingType.getConstructionCost());
    }

    @Override
    protected void initializeMaintenanceCost() {
        maintenanceCost.putAll(housingType.getMaintenanceCost());
    }

    @Override
    protected int calculateProduction() {
        return 0;
    }

    @Override
    protected int calculateBaseResourceConsumption(ResourceType type) {
        switch (type) {
            case FOOD:
                return (int) Math.ceil(occupied * housingType.getFoodPerColonist());
            case WATER:
                return (int) Math.ceil(occupied * housingType.getWaterPerColonist());
            case ENERGY:
                return housingType.getBaseEnergyCost() +
                        (int) Math.ceil(occupied * housingType.getEnergyPerColonist());
            default:
                return 0;
        }
    }

    /**
     * Gets the type of housing.
     *
     * @return The housing type
     */
    public UnlockedHousingType getHousingType() {
        return housingType;
    }

    /**
     * Enum for different types of unlocked housing.
     */
    public enum UnlockedHousingType {
        BIODOME("Biodome", "Self-contained ecological habitat system",
                50, 0.9f, 4, 6, 12, "biodome",
                0.8f, 0.7f, 0.6f, 15,
                Map.of(ResourceType.MATERIALS, 300, ResourceType.ENERGY, 150, ResourceType.RARE_MINERALS, 50),
                Map.of(ResourceType.ENERGY, 12, ResourceType.WATER, 8));

        private final String name;
        private final String description;
        private final int capacity;
        private final float comfortLevel;
        private final int growthRate;
        private final int workersRequired;
        private final int constructionTime;
        private final String unlockId;
        private final float foodPerColonist;
        private final float waterPerColonist;
        private final float energyPerColonist;
        private final int baseEnergyCost;
        private final Map<ResourceType, Integer> constructionCost;
        private final Map<ResourceType, Integer> maintenanceCost;

        UnlockedHousingType(String name, String description,
                            int capacity, float comfortLevel, int growthRate,
                            int workersRequired, int constructionTime,
                            String unlockId,
                            float foodPerColonist, float waterPerColonist,
                            float energyPerColonist, int baseEnergyCost,
                            Map<ResourceType, Integer> constructionCost,
                            Map<ResourceType, Integer> maintenanceCost) {
            this.name = name;
            this.description = description;
            this.capacity = capacity;
            this.comfortLevel = comfortLevel;
            this.growthRate = growthRate;
            this.workersRequired = workersRequired;
            this.constructionTime = constructionTime;
            this.unlockId = unlockId;
            this.foodPerColonist = foodPerColonist;
            this.waterPerColonist = waterPerColonist;
            this.energyPerColonist = energyPerColonist;
            this.baseEnergyCost = baseEnergyCost;
            this.constructionCost = constructionCost;
            this.maintenanceCost = maintenanceCost;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
        public int getCapacity() { return capacity; }
        public float getComfortLevel() { return comfortLevel; }
        public int getGrowthRate() { return growthRate; }
        public int getWorkersRequired() { return workersRequired; }
        public int getConstructionTime() { return constructionTime; }
        public String getUnlockId() { return unlockId; }
        public float getFoodPerColonist() { return foodPerColonist; }
        public float getWaterPerColonist() { return waterPerColonist; }
        public float getEnergyPerColonist() { return energyPerColonist; }
        public int getBaseEnergyCost() { return baseEnergyCost; }
        public Map<ResourceType, Integer> getConstructionCost() { return constructionCost; }
        public Map<ResourceType, Integer> getMaintenanceCost() { return maintenanceCost; }
    }
}