package com.colonygenesis.core;

import com.colonygenesis.building.AbstractBuilding;
import com.colonygenesis.building.HabitationBuilding;
import com.colonygenesis.map.Tile;
import com.colonygenesis.ui.events.ColonyEvents;
import com.colonygenesis.ui.events.EventBus;
import com.colonygenesis.util.LoggerUtil;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Manages the colony's population, housing, and worker assignments.
 */
public class ColonyManager implements Serializable {
    private static final Logger LOGGER = LoggerUtil.getLogger(ColonyManager.class);

    @Serial
    private static final long serialVersionUID = 1L;

    private final Game game;
    private String colonyName;
    private int totalPopulation;
    private int availableWorkers;
    private int totalHousingCapacity;
    private int availableHousingCapacity;
    private final List<HabitationBuilding> housingBuildings;

    // Event bus for publishing events
    private final transient EventBus eventBus;

    /**
     * Constructs a new colony manager for the specified game.
     *
     * @param game The game
     */
    public ColonyManager(Game game) {
        this.game = game;
        this.colonyName = game.getColonyName();
        this.totalPopulation = 10; // Starting population
        this.availableWorkers = 10;
        this.totalHousingCapacity = 10;
        this.availableHousingCapacity = 0;
        this.housingBuildings = new ArrayList<>();
        this.eventBus = EventBus.getInstance();

        LOGGER.info("ColonyManager initialized with population: " + totalPopulation);

        // Publish initial population state
        publishPopulationChanged(0);
    }

    /**
     * Adds a new housing building to the colony.
     *
     * @param building The housing building to add
     */
    public void addHousingBuilding(HabitationBuilding building) {
        housingBuildings.add(building);
        int previousCapacity = totalHousingCapacity;
        totalHousingCapacity += building.getCapacity();
        availableHousingCapacity += building.getCapacity();
        LOGGER.info("Added housing building: " + building.getName() +
                " with capacity " + building.getCapacity());

        // Publish housing capacity changed event
        eventBus.publish(new ColonyEvents.HousingCapacityChangedEvent(
                totalHousingCapacity, previousCapacity));
    }

    /**
     * Assigns workers to a building.
     *
     * @param building The building to assign workers to
     * @param workers The number of workers to assign
     * @return The number of workers actually assigned
     */
    public int assignWorkers(AbstractBuilding building, int workers) {
        int previousAvailable = availableWorkers;
        int toAssign = Math.min(workers, availableWorkers);

        if (toAssign <= 0) {
            return 0;
        }

        int assigned = building.assignWorkers(toAssign);
        availableWorkers -= assigned;

        LOGGER.info("Assigned " + assigned + " workers to " + building.getName() +
                ". Available workers: " + availableWorkers);

        // Publish worker availability changed event
        eventBus.publish(new ColonyEvents.WorkerAvailabilityChangedEvent(
                availableWorkers, previousAvailable));

        return assigned;
    }

    /**
     * Removes workers from a building and returns them to the available pool.
     *
     * @param building The building to remove workers from
     * @param workers The number of workers to remove
     * @return The number of workers actually removed
     */
    public int removeWorkers(AbstractBuilding building, int workers) {
        int previousAvailable = availableWorkers;
        int removed = building.removeWorkers(workers);
        availableWorkers += removed;

        LOGGER.info("Removed " + removed + " workers from " + building.getName() +
                ". Available workers: " + availableWorkers);

        // Publish worker availability changed event
        eventBus.publish(new ColonyEvents.WorkerAvailabilityChangedEvent(
                availableWorkers, previousAvailable));

        return removed;
    }

    /**
     * Processes the turn for the colony, including population growth.
     */
    public void processTurn() {
        int previousPopulation = totalPopulation;

        // Calculate new births based on housing conditions
        int newBirths = 0;

        for (HabitationBuilding building : housingBuildings) {
            if (building.isActive()) {
                // Calculate growth for this building
                int growth = building.getPopulationGrowthRate();

                // Apply comfort level modifier
                growth = (int) (growth * building.getComfortLevel());

                // Only grow if there's available capacity
                growth = Math.min(growth, availableHousingCapacity);

                if (growth > 0) {
                    // Add occupants to this building
                    int added = building.addOccupants(growth);
                    newBirths += added;
                    availableHousingCapacity -= added;

                    LOGGER.fine("Population grew by " + added + " in " + building.getName());
                }
            }
        }

        // Update total population and available workers
        totalPopulation += newBirths;
        availableWorkers += newBirths;

        LOGGER.info("Colony processed turn. New births: " + newBirths +
                ", Total population: " + totalPopulation);

        // Only publish event if population actually changed
        if (newBirths > 0) {
            publishPopulationChanged(previousPopulation);
        }
    }

    /**
     * Gets the total population of the colony.
     *
     * @return The total population
     */
    public int getTotalPopulation() {
        return totalPopulation;
    }

    /**
     * Gets the number of available workers (not assigned to buildings).
     *
     * @return The number of available workers
     */
    public int getAvailableWorkers() {
        return availableWorkers;
    }

    /**
     * Gets the total housing capacity of the colony.
     *
     * @return The total housing capacity
     */
    public int getTotalHousingCapacity() {
        return totalHousingCapacity;
    }

    /**
     * Gets the name of the colony.
     *
     * @return The colony name
     */
    public String getColonyName() {
        return colonyName;
    }

    /**
     * Sets the name of the colony.
     *
     * @param colonyName The new colony name
     */
    public void setColonyName(String colonyName) {
        this.colonyName = colonyName;
    }

    /**
     * Publishes a population changed event with current values.
     *
     * @param previousPopulation The previous total population
     */
    private void publishPopulationChanged(int previousPopulation) {
        eventBus.publish(new ColonyEvents.PopulationChangedEvent(
                totalPopulation,
                totalHousingCapacity,
                availableWorkers,
                previousPopulation
        ));
    }
}