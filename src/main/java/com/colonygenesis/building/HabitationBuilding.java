package com.colonygenesis.building;

import com.colonygenesis.map.Tile;
import com.colonygenesis.resource.ResourceType;

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

    protected int capacity;  // Max population this building can house
    protected int occupied;  // Current number of occupants
    protected float comfortLevel;  // Comfort level (affects morale, growth)
    protected int populationGrowthRate;  // Population growth per turn (can be 0)

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
                              int capacity, float comfortLevel, int populationGrowthRate) {
        super(name, description, location, constructionTime, workersRequired, BuildingType.HABITATION);

        this.capacity = capacity;
        this.occupied = 0;
        this.comfortLevel = comfortLevel;
        this.populationGrowthRate = populationGrowthRate;
    }

    @Override
    public Map<ResourceType, Integer> operate() {
        Map<ResourceType, Integer> output = new EnumMap<>(ResourceType.class);

        if (isActive() && occupied < capacity && populationGrowthRate > 0) {
            // Calculate population growth - only if the building is not at capacity
            int growthThisTurn = Math.min(populationGrowthRate, capacity - occupied);
            if (growthThisTurn > 0) {
                occupied += growthThisTurn;
                LOGGER.fine(getName() + " generated " + growthThisTurn + " new colonists");
            }
        }

        // Habitation buildings consume resources rather than produce them
        // Typically they consume food, water, energy based on occupants
        calculateResourceConsumption(output);

        return output;
    }

    /**
     * Calculates resource consumption based on occupants.
     * Must be implemented by subclasses to determine specific consumption.
     *
     * @param output Map to store resource consumption
     */
    protected abstract void calculateResourceConsumption(Map<ResourceType, Integer> output);

    /**
     * Adds occupants to the habitation building.
     *
     * @param count Number of occupants to add
     * @return Number of occupants actually added
     */
    public int addOccupants(int count) {
        int availableSpace = capacity - occupied;
        int actualAdded = Math.min(count, availableSpace);

        occupied += actualAdded;
        LOGGER.fine("Added " + actualAdded + " occupants to " + getName() +
                " (" + occupied + "/" + capacity + ")");

        return actualAdded;
    }

    /**
     * Removes occupants from the habitation building.
     *
     * @param count Number of occupants to remove
     * @return Number of occupants actually removed
     */
    public int removeOccupants(int count) {
        int actualRemoved = Math.min(count, occupied);

        occupied -= actualRemoved;
        LOGGER.fine("Removed " + actualRemoved + " occupants from " + getName() +
                " (" + occupied + "/" + capacity + ")");

        return actualRemoved;
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
}