package com.colonygenesis.building;

import com.colonygenesis.core.Game;
import com.colonygenesis.map.Tile;
import com.colonygenesis.resource.ResourceType;
import com.colonygenesis.ui.events.ColonyEvents;
import com.colonygenesis.ui.events.EventBus;

import java.io.Serial;
import java.util.EnumMap;
import java.util.Map;

/**
 * Abstract class for buildings that provide habitation for colonists.
 * Second layer in the building hierarchy.
 */
public abstract class HabitationBuilding extends AbstractBuilding {
    @Serial
    private static final long serialVersionUID = 1L;

    protected int capacity;
    protected int occupied;
    protected float comfortLevel;
    protected int populationGrowthRate;

    /**
     * Constructs a new habitation building.
     *
     * @param name The name of the building
     * @param description A brief description of the building
     * @param location The tile where the building is located
     * @param constructionTime The number of turns to construct the building
     * @param workersRequired The number of workers required to operate the building
     * @param capacity The maximum population capacity
     * @param comfortLevel The comfort level of the habitation (0.0-1.0)
     * @param populationGrowthRate Natural population growth per turn
     */
    public HabitationBuilding(String name, String description, Tile location,
                              int constructionTime, int workersRequired,
                              int capacity, float comfortLevel, int populationGrowthRate, Game game) {
        super(name, description, location, constructionTime, workersRequired, BuildingType.HABITATION, game);

        this.capacity = capacity;
        this.occupied = 0;
        this.comfortLevel = comfortLevel;
        this.populationGrowthRate = populationGrowthRate;
    }

    @Override
    public Map<ResourceType, Integer> operate() {
        Map<ResourceType, Integer> output = new EnumMap<>(ResourceType.class);

        calculateResourceConsumption(output);

        return output;
    }

    /**
     * Calculates resource consumption based on occupants.
     * Must be implemented by subclasses to determine specific consumption.
     *
     * @param output Map to store resource consumption
     */
    @Override
    protected void calculateResourceConsumption(Map<ResourceType, Integer> output) {
        if (!isActive() || occupied == 0) {
            return;
        }

        // Get consumption values from building
        int baseFood = calculateBaseResourceConsumption(ResourceType.FOOD);
        int baseWater = calculateBaseResourceConsumption(ResourceType.WATER);
        int baseEnergy = calculateBaseResourceConsumption(ResourceType.ENERGY);

        // Apply tech modifiers
        if (game != null && game.getTechManager() != null) {
            double foodModifier = game.getTechManager().getConsumptionModifier(ResourceType.FOOD, buildingType);
            double waterModifier = game.getTechManager().getConsumptionModifier(ResourceType.WATER, buildingType);
            double energyModifier = game.getTechManager().getConsumptionModifier(ResourceType.ENERGY, buildingType);

            baseFood = (int) Math.ceil(baseFood * foodModifier);
            baseWater = (int) Math.ceil(baseWater * waterModifier);
            baseEnergy = (int) Math.ceil(baseEnergy * energyModifier);
        }

        output.put(ResourceType.FOOD, -baseFood);
        output.put(ResourceType.WATER, -baseWater);
        output.put(ResourceType.ENERGY, -baseEnergy);
    }

    // Add method to calculate base consumption before modifiers
    protected abstract int calculateBaseResourceConsumption(ResourceType type);

    // Add method to get modified growth rate
    public int getModifiedPopulationGrowthRate() {
        if (game != null && game.getTechManager() != null) {
            return (int) Math.ceil(populationGrowthRate * game.getTechManager().getPopulationGrowthModifier());
        }
        return populationGrowthRate;
    }

    /**
     * Gets the population capacity of this building.
     *
     * @return The maximum population capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Gets the current number of occupants.
     *
     * @return The current occupancy
     */
    public int getOccupied() {
        return occupied;
    }

    /**
     * Gets the comfort level of this habitation.
     *
     * @return The comfort level (0.0-1.0)
     */
    public float getComfortLevel() {
        return comfortLevel;
    }

    /**
     * Gets the natural population growth rate per turn.
     *
     * @return The population growth rate
     */
    public int getPopulationGrowthRate() {
        return populationGrowthRate;
    }

    /**
     * Gets the occupancy percentage.
     *
     * @return The occupancy percentage (0-100)
     */
    public int getOccupancyPercentage() {
        if (capacity == 0) return 0;
        return (int)((float)occupied / capacity * 100);
    }

    public void setOccupied(int occupied) {
        int previousOccupied = this.occupied;
        this.occupied = Math.min(occupied, this.capacity); // Ensure we don't exceed capacity

        // Publish the occupancy changed event
        if (this.occupied != previousOccupied) {
            EventBus.getInstance().publish(new ColonyEvents.BuildingOccupancyChangedEvent(
                    this, this.occupied, previousOccupied, this.capacity));
        }
    }
}