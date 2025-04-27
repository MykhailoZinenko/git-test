package com.colonygenesis.resource;

import com.colonygenesis.ui.events.EventBus;
import com.colonygenesis.ui.events.ResourceEvents;
import com.colonygenesis.util.LoggerUtil;
import com.colonygenesis.util.Result;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

/**
 * Manages the resources in the game.
 * Handles resource storage, production, consumption, and tracking.
 */
public class ResourceManager implements Serializable {
    private static final Logger LOGGER = LoggerUtil.getLogger(ResourceManager.class);
    @Serial
    private static final long serialVersionUID = 1L;

    private final Map<ResourceType, Integer> resources;
    private final Map<ResourceType, Integer> capacity;
    private final Map<ResourceType, Integer> production;
    private final Map<ResourceType, Integer> consumption;
    private final Map<ResourceType, Integer> lastTurnResources;

    private int assignedWorkers;
    private int populationGrowthRate;

    private transient EventBus eventBus;

    /**
     * Constructs a resource manager for the specified game.
     */
    public ResourceManager() {
        LOGGER.fine("Initializing ResourceManager");

        this.eventBus = EventBus.getInstance();

        resources = new EnumMap<>(ResourceType.class);
        capacity = new EnumMap<>(ResourceType.class);
        production = new EnumMap<>(ResourceType.class);
        consumption = new EnumMap<>(ResourceType.class);
        lastTurnResources = new EnumMap<>(ResourceType.class);

        this.assignedWorkers = 0;

        for (ResourceType type : ResourceType.values()) {
            resources.put(type, 0);
            capacity.put(type, type.getBaseStorage());
            production.put(type, 0);
            consumption.put(type, 0);
            lastTurnResources.put(type, 0);
        }

        resources.put(ResourceType.FOOD, 500);
        resources.put(ResourceType.WATER, 500);
        resources.put(ResourceType.MATERIALS, 500);
        resources.put(ResourceType.ENERGY, 300);
        resources.put(ResourceType.POPULATION, 10);
        resources.put(ResourceType.ALIEN_COMPOUNDS, 2);

        LOGGER.info("ResourceManager initialized with starting resources");

        publishResourcesUpdated();
    }

    /**
     * Gets the current amount of a specific resource.
     *
     * @param type The resource type
     * @return The amount of the resource
     */
    public int getResource(ResourceType type) {
        return resources.getOrDefault(type, 0);
    }

    /**
     * Gets the number of available workers (population not assigned to buildings).
     *
     * @return The number of available workers
     */
    public int getAvailableWorkers() {
        return Math.max(0, getResource(ResourceType.POPULATION) - assignedWorkers);
    }

    /**
     * Gets the total number of workers assigned to buildings.
     *
     * @return The number of assigned workers
     */
    public int getAssignedWorkers() {
        return assignedWorkers;
    }

    /**
     * Gets a map of all current resource amounts.
     *
     * @return A defensive copy of the resources map
     */
    public Map<ResourceType, Integer> getAllResources() {
        return new EnumMap<>(resources);
    }

    /**
     * Gets a map of all resource production values.
     *
     * @return A defensive copy of the production map
     */
    public Map<ResourceType, Integer> getAllProduction() {
        return new EnumMap<>(production);
    }

    /**
     * Gets a map of all resource consumption values.
     *
     * @return A defensive copy of the consumption map
     */
    public Map<ResourceType, Integer> getAllConsumption() {
        return new EnumMap<>(consumption);
    }

    /**
     * Gets a map of net production for all resources.
     *
     * @return A map of resources to their net production values
     */
    public Map<ResourceType, Integer> getAllNetProduction() {
        Map<ResourceType, Integer> netProduction = new EnumMap<>(ResourceType.class);

        for (ResourceType type : ResourceType.values()) {
            netProduction.put(type, getNetProduction(type));
        }

        return netProduction;
    }

    /**
     * Gets the storage capacity for a specific resource.
     *
     * @param type The resource type
     * @return The storage capacity
     */
    public int getCapacity(ResourceType type) {
        return capacity.getOrDefault(type, 0);
    }

    /**
     * Gets a map of all storage capacities.
     *
     * @return A defensive copy of the capacity map
     */
    public Map<ResourceType, Integer> getAllCapacity() {
        return new EnumMap<>(capacity);
    }

    /**
     * Gets the production amount for a specific resource.
     *
     * @param type The resource type
     * @return The production amount
     */
    public int getProduction(ResourceType type) {
        return production.getOrDefault(type, 0);
    }

    /**
     * Gets the consumption amount for a specific resource.
     *
     * @param type The resource type
     * @return The consumption amount
     */
    public int getConsumption(ResourceType type) {
        return consumption.getOrDefault(type, 0);
    }

    /**
     * Gets the net production (production - consumption) for a specific resource.
     *
     * @param type The resource type
     * @return The net production
     */
    public int getNetProduction(ResourceType type) {
        return getProduction(type) - getConsumption(type);
    }

    /**
     * Adds an amount of a resource to the player's stockpile.
     *
     * @param type   The resource type
     * @param amount The amount to add
     * @return A Result indicating success or failure
     */
    public Result<Integer> addResource(ResourceType type, int amount) {
        if (type == null) {
            return Result.failure("Resource type cannot be null");
        }

        if (amount <= 0) {
            return Result.failure("Amount must be positive");
        }

        int current = resources.getOrDefault(type, 0);
        int cap = capacity.getOrDefault(type, 0);

        if (type.isStorable() && current + amount > cap) {
            resources.put(type, cap);

            int actualAdded = cap - current;
            LOGGER.warning(String.format("Resource %s at capacity: %d/%d. Wasted %d units",
                    type.getName(), cap, cap, amount - actualAdded));

            eventBus.publish(new ResourceEvents.ResourceChangedEvent(type, cap, current));

            return Result.failure(String.format("Storage at capacity. Added %d of %d %s",
                    actualAdded, amount, type.getName()));
        } else {
            resources.put(type, current + amount);

            LOGGER.fine(String.format("Added %d %s. New total: %d",
                    amount, type.getName(), current + amount));

            eventBus.publish(new ResourceEvents.ResourceChangedEvent(type, current + amount, current));

            return Result.success(amount);
        }
    }

    /**
     * Removes an amount of a resource from the player's stockpile.
     *
     * @param type   The resource type
     * @param amount The amount to remove
     * @return A Result indicating success or failure
     */
    public Result<Integer> removeResource(ResourceType type, int amount) {
        if (type == null) {
            return Result.failure("Resource type cannot be null");
        }

        if (amount <= 0) {
            return Result.failure("Amount must be positive");
        }

        int current = resources.getOrDefault(type, 0);

        if (current < amount) {
            String error = String.format("Not enough %s: %d/%d needed",
                    type.getName(), current, amount);
            LOGGER.warning(error);
            resources.put(type, 0);
            return Result.failure(error);
        }

        resources.put(type, current - amount);

        LOGGER.fine(String.format("Removed %d %s. New total: %d",
                amount, type.getName(), current - amount));

        eventBus.publish(new ResourceEvents.ResourceChangedEvent(type, current - amount, current));

        return Result.success(amount);
    }

    /**
     * Sets the production rate for a resource.
     *
     * @param type  The resource type
     * @param value The production value
     */
    public void setProduction(ResourceType type, int value) {
        int previousValue = production.getOrDefault(type, 0);
        production.put(type, value);
        LOGGER.fine(String.format("Set %s production to %d",
                type.getName(), value));

        eventBus.publish(new ResourceEvents.ResourceChangedEvent(type, value, previousValue, true));
    }

    /**
     * Sets the consumption rate for a resource.
     *
     * @param type  The resource type
     * @param value The consumption value
     */
    public void setConsumption(ResourceType type, int value) {
        int previousValue = consumption.getOrDefault(type, 0);
        consumption.put(type, value);
        LOGGER.fine(String.format("Set %s consumption to %d",
                type.getName(), value));

        publishResourcesUpdated();
    }

    /**
     * Increases the storage capacity for a resource.
     *
     * @param type   The resource type
     * @param amount The amount to increase by
     * @return The new capacity
     */
    public int increaseCapacity(ResourceType type, int amount) {
        int current = capacity.getOrDefault(type, 0);
        int newCapacity = current + amount;
        capacity.put(type, newCapacity);

        LOGGER.info(String.format("Increased %s capacity by %d. New capacity: %d",
                type.getName(), amount, newCapacity));

        publishResourcesUpdated();

        return newCapacity;
    }

    /**
     * Adds to the production rate for a resource.
     *
     * @param type  The resource type
     * @param amount The amount to add to production
     * @return The new production value
     */
    public int addProduction(ResourceType type, int amount) {
        if (type == null) {
            throw new IllegalArgumentException("Resource type cannot be null");
        }

        int currentProduction = production.getOrDefault(type, 0);
        int newProduction = currentProduction + amount;
        production.put(type, newProduction);

        LOGGER.fine(String.format("Added %d to %s production. New production: %d",
                amount, type.getName(), newProduction));

        eventBus.publish(new ResourceEvents.ResourceChangedEvent(type, newProduction, currentProduction, true));

        return newProduction;
    }

    /**
     * Adds to the consumption rate for a resource.
     *
     * @param type  The resource type
     * @param amount The amount to add to consumption
     * @return The new consumption value
     */
    public int addConsumption(ResourceType type, int amount) {
        if (type == null) {
            throw new IllegalArgumentException("Resource type cannot be null");
        }

        int currentConsumption = consumption.getOrDefault(type, 0);
        int newConsumption = currentConsumption + amount;
        consumption.put(type, newConsumption);

        LOGGER.fine(String.format("Added %d to %s consumption. New consumption: %d",
                amount, type.getName(), newConsumption));

        publishResourcesUpdated();

        return newConsumption;
    }

    /**
     * Removes from the production rate for a resource.
     *
     * @param type  The resource type
     * @param amount The amount to remove from production
     * @return The new production value (will not go below 0)
     */
    public int removeProduction(ResourceType type, int amount) {
        if (type == null) {
            throw new IllegalArgumentException("Resource type cannot be null");
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        int currentProduction = production.getOrDefault(type, 0);
        int newProduction = Math.max(0, currentProduction - amount);
        production.put(type, newProduction);

        LOGGER.fine(String.format("Removed %d from %s production. New production: %d",
                amount, type.getName(), newProduction));

        eventBus.publish(new ResourceEvents.ResourceChangedEvent(type, newProduction, currentProduction, true));

        return newProduction;
    }

    /**
     * Removes from the consumption rate for a resource.
     *
     * @param type  The resource type
     * @param amount The amount to remove from consumption
     * @return The new consumption value (will not go below 0)
     */
    public int removeConsumption(ResourceType type, int amount) {
        if (type == null) {
            throw new IllegalArgumentException("Resource type cannot be null");
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        int currentConsumption = consumption.getOrDefault(type, 0);
        int newConsumption = Math.max(0, currentConsumption - amount);
        consumption.put(type, newConsumption);

        LOGGER.fine(String.format("Removed %d from %s consumption. New consumption: %d",
                amount, type.getName(), newConsumption));

        publishResourcesUpdated();

        return newConsumption;
    }

    /**
     * Assigns workers to a building.
     *
     * @param workersToAssign Number of workers to assign
     * @return Actual number of workers assigned
     */
    public int assignWorkers(int workersToAssign) {
        int availableWorkers = getAvailableWorkers();
        int actualAssigned = Math.min(workersToAssign, availableWorkers);

        if (actualAssigned <= 0) {
            return 0;
        }

        assignedWorkers += actualAssigned;

        LOGGER.info(String.format("Assigned %d workers. Total assigned: %d, Available: %d",
                actualAssigned, assignedWorkers, getAvailableWorkers()));

        eventBus.publish(new ResourceEvents.WorkerAvailabilityChangedEvent(
                getAvailableWorkers(),
                availableWorkers,
                actualAssigned
        ));

        return actualAssigned;
    }

    /**
     * Unassigns workers from a building.
     *
     * @param workersToRemove Number of workers to unassign
     * @return Actual number of workers unassigned
     */
    public int unassignWorkers(int workersToRemove) {
        int actualRemoved = Math.min(workersToRemove, assignedWorkers);

        if (actualRemoved <= 0) {
            return 0;
        }

        assignedWorkers -= actualRemoved;

        LOGGER.info(String.format("Unassigned %d workers. Total assigned: %d, Available: %d",
                actualRemoved, assignedWorkers, getAvailableWorkers()));

        eventBus.publish(new ResourceEvents.WorkerAvailabilityChangedEvent(
                getAvailableWorkers(),
                getAvailableWorkers() - actualRemoved,
                -actualRemoved
        ));

        return actualRemoved;
    }

    /**
     * Processes population growth for the turn.
     */
    private void processPopulationGrowth() {
        int currentPopulation = getResource(ResourceType.POPULATION);
        int housingCapacity = getCapacity(ResourceType.POPULATION);

        if (currentPopulation >= housingCapacity) {
            LOGGER.info("Population at capacity. No growth.");
            return;
        }

        int growthPotential = Math.min(populationGrowthRate, housingCapacity - currentPopulation);

        if (growthPotential > 0) {
            addResource(ResourceType.POPULATION, growthPotential);



            LOGGER.info("Population grew by " + growthPotential + ". New total: " + getResource(ResourceType.POPULATION));
        }
    }

    /**
     * Processes resource production and consumption for the current turn.
     */
    public void processTurn() {
        LOGGER.info("Processing resource turn");

        for (ResourceType type : ResourceType.values()) {
            lastTurnResources.put(type, resources.get(type));
        }

        StringBuilder resourceReport = new StringBuilder("Resource changes:\n");

        for (ResourceType type : ResourceType.values()) {
            int net = getNetProduction(type);

            if (net > 0) {
                Result<Integer> result = addResource(type, net);
                resourceReport.append(type.getName()).append(": +").append(net);

                if (result.isFailure()) {
                    resourceReport.append(" (").append(result.getErrorMessage()).append(")");
                }

                resourceReport.append("\n");
            } else if (net < 0) {
                int needed = -net;
                Result<Integer> result = removeResource(type, needed);

                if (result.isFailure()) {
                    LOGGER.fine("Game Over!");
                } else {
                    resourceReport.append(type.getName()).append(": ").append(net).append("\n");
                }
            }
        }

        processPopulationGrowth();

        LOGGER.info(resourceReport.toString());

        publishResourcesUpdated();
    }

    /**
     * Gets the change in resource amounts from the previous turn.
     *
     * @return A map of resources to their change values
     */
    public Map<ResourceType, Integer> getResourceChanges() {
        Map<ResourceType, Integer> changes = new EnumMap<>(ResourceType.class);

        for (ResourceType type : ResourceType.values()) {
            int current = resources.get(type);
            int previous = lastTurnResources.get(type);
            changes.put(type, current - previous);
        }

        return changes;
    }

    /**
     * Publishes a resources updated event to refresh the UI.
     */
    private void publishResourcesUpdated() {
        eventBus.publish(new ResourceEvents.ResourcesUpdatedEvent(
                new EnumMap<>(resources),
                getAllNetProduction(),
                new EnumMap<>(capacity),
                getAvailableWorkers(),
                assignedWorkers
        ));
    }

    @Serial
    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        this.eventBus = EventBus.getInstance();
        LOGGER.info("ResourceManager deserialized and transient fields reinitialized");
    }

    /**
     * Publishes the current resource state to update UI components.
     * Call this when initializing UI or after loading a game.
     */
    public void publishCurrentState() {
        eventBus.publish(new ResourceEvents.ResourcesUpdatedEvent(
                new EnumMap<>(resources),
                getAllNetProduction(),
                new EnumMap<>(capacity),
                getAvailableWorkers(),
                assignedWorkers
        ));
    }
}