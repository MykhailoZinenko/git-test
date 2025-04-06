package com.colonygenesis.building;

import com.colonygenesis.map.Tile;
import com.colonygenesis.resource.ResourceType;

import java.io.Serial;
import java.util.Map;

/**
 * Basic housing structures that provide essential living space for colonists.
 * Third layer in the building hierarchy.
 */
public class BasicHousing extends HabitationBuilding {
    @Serial
    private static final long serialVersionUID = 1L;

    private final HousingType housingType;

    /**
     * Constructs a new basic housing building.
     *
     * @param housingType The type of housing
     * @param location The tile where the building is located
     */
    public BasicHousing(HousingType housingType, Tile location) {
        super(
                housingType.getName(),
                housingType.getDescription(),
                location,
                housingType.getConstructionTime(),
                housingType.getWorkersRequired(),
                housingType.getCapacity(),
                housingType.getComfortLevel(),
                housingType.getGrowthRate()
        );

        this.housingType = housingType;

        for (Map.Entry<ResourceType, Integer> entry : housingType.getConstructionCost().entrySet()) {
            constructionCost.put(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<ResourceType, Integer> entry : housingType.getMaintenanceCost().entrySet()) {
            maintenanceCost.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    protected void initializeConstructionCost() {
        for (Map.Entry<ResourceType, Integer> entry : housingType.getConstructionCost().entrySet()) {
            constructionCost.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    protected void initializeMaintenanceCost() {
        for (Map.Entry<ResourceType, Integer> entry : housingType.getMaintenanceCost().entrySet()) {
            maintenanceCost.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    protected void calculateResourceConsumption(Map<ResourceType, Integer> output) {
        if (!isActive() || occupied == 0) {
            return;
        }

        int foodConsumption = (int) Math.ceil(occupied * housingType.getFoodPerColonist());
        int waterConsumption = (int) Math.ceil(occupied * housingType.getWaterPerColonist());
        int energyConsumption = housingType.getBaseEnergyCost() +
                (int) Math.ceil(occupied * housingType.getEnergyPerColonist());

        output.put(ResourceType.FOOD, -foodConsumption);
        output.put(ResourceType.WATER, -waterConsumption);
        output.put(ResourceType.ENERGY, -energyConsumption);
    }

    /**
     * Gets the type of housing.
     *
     * @return The housing type
     */
    public HousingType getHousingType() {
        return housingType;
    }

    /**
     * Enum for different types of basic housing.
     */
    public enum HousingType {
        HABITAT_DOME("Habitat Dome", "Basic pressurized living quarters",
                15, 0.5f, 1, 2, 2,
                1.0f, 0.5f, 0.3f, 5,
                Map.of(ResourceType.MATERIALS, 80, ResourceType.ENERGY, 30),
                Map.of(ResourceType.ENERGY, 5, ResourceType.MATERIALS, 1)),

        EMERGENCY_SHELTER("Emergency Shelter", "Temporary housing for colonists",
                10, 0.3f, 0, 1, 1,
                0.8f, 0.4f, 0.2f, 3,
                Map.of(ResourceType.MATERIALS, 40, ResourceType.ENERGY, 15),
                Map.of(ResourceType.ENERGY, 3)),

        PREFAB_QUARTERS("Prefabricated Quarters", "Modular housing units",
                20, 0.6f, 1, 3, 3,
                1.0f, 0.5f, 0.4f, 6,
                Map.of(ResourceType.MATERIALS, 100, ResourceType.ENERGY, 40),
                Map.of(ResourceType.ENERGY, 7, ResourceType.MATERIALS, 2));

        private final String name;
        private final String description;
        private final int capacity;
        private final float comfortLevel;
        private final int growthRate;
        private final int workersRequired;
        private final int constructionTime;
        private final float foodPerColonist;
        private final float waterPerColonist;
        private final float energyPerColonist;
        private final int baseEnergyCost;
        private final Map<ResourceType, Integer> constructionCost;
        private final Map<ResourceType, Integer> maintenanceCost;

        HousingType(String name, String description,
                    int capacity, float comfortLevel, int growthRate,
                    int workersRequired, int constructionTime,
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
            this.foodPerColonist = foodPerColonist;
            this.waterPerColonist = waterPerColonist;
            this.energyPerColonist = energyPerColonist;
            this.baseEnergyCost = baseEnergyCost;
            this.constructionCost = constructionCost;
            this.maintenanceCost = maintenanceCost;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public int getCapacity() {
            return capacity;
        }

        public float getComfortLevel() {
            return comfortLevel;
        }

        public int getGrowthRate() {
            return growthRate;
        }

        public int getWorkersRequired() {
            return workersRequired;
        }

        public int getConstructionTime() {
            return constructionTime;
        }

        public float getFoodPerColonist() {
            return foodPerColonist;
        }

        public float getWaterPerColonist() {
            return waterPerColonist;
        }

        public float getEnergyPerColonist() {
            return energyPerColonist;
        }

        public int getBaseEnergyCost() {
            return baseEnergyCost;
        }

        public Map<ResourceType, Integer> getConstructionCost() {
            return constructionCost;
        }

        public Map<ResourceType, Integer> getMaintenanceCost() {
            return maintenanceCost;
        }
    }
}