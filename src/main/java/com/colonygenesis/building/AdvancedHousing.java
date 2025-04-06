package com.colonygenesis.building;

import com.colonygenesis.map.Tile;
import com.colonygenesis.resource.ResourceType;

import java.io.Serial;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Advanced housing structures with better amenities and special features.
 * Third layer in the building hierarchy.
 */
public class AdvancedHousing extends HabitationBuilding {
    @Serial
    private static final long serialVersionUID = 1L;

    private final HousingType housingType;
    private final Set<Amenity> amenities;
    private float moraleBonus;

    /**
     * Constructs a new advanced housing building.
     *
     * @param housingType The type of advanced housing
     * @param location The tile where the building is located
     */
    public AdvancedHousing(HousingType housingType, Tile location) {
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
        this.amenities = new HashSet<>();
        this.moraleBonus = housingType.getBaseMoraleBonus();

        for (Map.Entry<ResourceType, Integer> entry : housingType.getConstructionCost().entrySet()) {
            constructionCost.put(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<ResourceType, Integer> entry : housingType.getMaintenanceCost().entrySet()) {
            maintenanceCost.put(entry.getKey(), entry.getValue());
        }

        for (Amenity amenity : housingType.getDefaultAmenities()) {
            addAmenity(amenity);
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

        for (Amenity amenity : amenities) {
            energyConsumption += amenity.getEnergyCost();
        }

        output.put(ResourceType.FOOD, -foodConsumption);
        output.put(ResourceType.WATER, -waterConsumption);
        output.put(ResourceType.ENERGY, -energyConsumption);
    }

    @Override
    public Map<ResourceType, Integer> operate() {
        Map<ResourceType, Integer> output = super.operate();

        if (isActive()) {
            if (housingType == HousingType.RESEARCH_QUARTERS) {
                output.put(ResourceType.RESEARCH, 2 + occupied / 5);
            }
        }

        return output;
    }

    /**
     * Adds an amenity to this housing building.
     *
     * @param amenity The amenity to add
     * @return true if the amenity was added, false if it was already present
     */
    public boolean addAmenity(Amenity amenity) {
        if (amenities.add(amenity)) {
            moraleBonus += amenity.getMoraleBonus();
            LOGGER.fine("Added amenity " + amenity + " to " + getName());
            return true;
        }
        return false;
    }

    /**
     * Removes an amenity from this housing building.
     *
     * @param amenity The amenity to remove
     * @return true if the amenity was removed, false if it wasn't present
     */
    public boolean removeAmenity(Amenity amenity) {
        if (amenities.remove(amenity)) {
            moraleBonus -= amenity.getMoraleBonus();
            LOGGER.fine("Removed amenity " + amenity + " from " + getName());
            return true;
        }
        return false;
    }

    /**
     * Gets the current morale bonus provided by this housing.
     *
     * @return The total morale bonus
     */
    public float getMoraleBonus() {
        return moraleBonus;
    }

    /**
     * Gets the set of amenities in this housing.
     *
     * @return A copy of the amenities set
     */
    public Set<Amenity> getAmenities() {
        return new HashSet<>(amenities);
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
     * Enum for housing amenities that improve living conditions.
     */
    public enum Amenity {
        RECREATION_ROOM("Recreation Room", "Provides entertainment for colonists", 0.05f, 3),
        HYDROPONICS("Hydroponics Bay", "Small food production within housing", 0.03f, 4),
        MEDICAL_BAY("Medical Bay", "Basic medical facilities", 0.04f, 5),
        COMMUNITY_CENTER("Community Center", "Improves social cohesion", 0.06f, 4),
        EDUCATION_CENTER("Education Center", "Facilities for learning and training", 0.05f, 3);

        private final String name;
        private final String description;
        private final float moraleBonus;
        private final int energyCost;

        Amenity(String name, String description, float moraleBonus, int energyCost) {
            this.name = name;
            this.description = description;
            this.moraleBonus = moraleBonus;
            this.energyCost = energyCost;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public float getMoraleBonus() {
            return moraleBonus;
        }

        public int getEnergyCost() {
            return energyCost;
        }
    }

    /**
     * Enum for different types of advanced housing.
     */
    public enum HousingType {
        RESIDENTIAL_COMPLEX("Residential Complex", "Modern living quarters with amenities",
                30, 0.8f, 2, 4, 5,
                1.2f, 0.6f, 0.5f, 10, 0.1f,
                Set.of(Amenity.RECREATION_ROOM),
                Map.of(ResourceType.MATERIALS, 150, ResourceType.ENERGY, 60),
                Map.of(ResourceType.ENERGY, 10, ResourceType.MATERIALS, 2)),

        GARDEN_HABITAT("Garden Habitat", "Living quarters with integrated green spaces",
                25, 0.9f, 3, 3, 6,
                1.0f, 0.8f, 0.4f, 8, 0.15f,
                Set.of(Amenity.HYDROPONICS),
                Map.of(ResourceType.MATERIALS, 140, ResourceType.ENERGY, 50),
                Map.of(ResourceType.ENERGY, 8, ResourceType.WATER, 5)),

        RESEARCH_QUARTERS("Research Quarters", "Living space optimized for scientific staff",
                20, 0.7f, 1, 2, 4,
                1.1f, 0.5f, 0.6f, 12, 0.05f,
                Set.of(Amenity.EDUCATION_CENTER),
                Map.of(ResourceType.MATERIALS, 120, ResourceType.ENERGY, 70),
                Map.of(ResourceType.ENERGY, 12, ResourceType.MATERIALS, 1));

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
        private final float baseMoraleBonus;
        private final Set<Amenity> defaultAmenities;
        private final Map<ResourceType, Integer> constructionCost;
        private final Map<ResourceType, Integer> maintenanceCost;

        HousingType(String name, String description,
                    int capacity, float comfortLevel, int growthRate,
                    int workersRequired, int constructionTime,
                    float foodPerColonist, float waterPerColonist,
                    float energyPerColonist, int baseEnergyCost,
                    float baseMoraleBonus, Set<Amenity> defaultAmenities,
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
            this.baseMoraleBonus = baseMoraleBonus;
            this.defaultAmenities = defaultAmenities;
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

        public float getBaseMoraleBonus() {
            return baseMoraleBonus;
        }

        public Set<Amenity> getDefaultAmenities() {
            return defaultAmenities;
        }

        public Map<ResourceType, Integer> getConstructionCost() {
            return constructionCost;
        }

        public Map<ResourceType, Integer> getMaintenanceCost() {
            return maintenanceCost;
        }
    }
}