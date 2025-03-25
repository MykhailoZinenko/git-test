package com.colonygenesis.resource;

import com.colonygenesis.core.Game;
import com.colonygenesis.util.LoggerUtil;
import com.colonygenesis.util.Result;

import java.io.Serial;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Manages the resources in the game.
 * Handles resource storage, production, consumption, and tracking.
 */
public class ResourceManager implements Serializable {
    private static final Logger LOGGER = LoggerUtil.getLogger(ResourceManager.class);
    @Serial
    private static final long serialVersionUID = 1L;

    private final Game game;

    private final Map<ResourceType, Integer> resources;
    private final Map<ResourceType, Integer> capacity;
    private final Map<ResourceType, Integer> production;
    private final Map<ResourceType, Integer> consumption;
    private final Map<ResourceType, Integer> lastTurnResources;

    /**
     * Constructs a resource manager for the specified game.
     *
     * @param game The game
     */
    public ResourceManager(Game game) {
        this.game = game;
        LOGGER.fine("Initializing ResourceManager");

        resources = new EnumMap<>(ResourceType.class);
        capacity = new EnumMap<>(ResourceType.class);
        production = new EnumMap<>(ResourceType.class);
        consumption = new EnumMap<>(ResourceType.class);
        lastTurnResources = new EnumMap<>(ResourceType.class);

        for (ResourceType type : ResourceType.values()) {
            resources.put(type, 0);
            capacity.put(type, type.getBaseStorage());
            production.put(type, 0);
            consumption.put(type, 0);
            lastTurnResources.put(type, 0);
        }

        resources.put(ResourceType.FOOD, 500);
        resources.put(ResourceType.WATER, 500);
        resources.put(ResourceType.MATERIALS, 1000);
        resources.put(ResourceType.ENERGY, 200);

        LOGGER.info("ResourceManager initialized with starting resources");
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

            return Result.failure(String.format("Storage at capacity. Added %d of %d %s",
                    actualAdded, amount, type.getName()));
        } else {
            resources.put(type, current + amount);

            LOGGER.fine(String.format("Added %d %s. New total: %d",
                    amount, type.getName(), current + amount));

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
            return Result.failure(error);
        }

        resources.put(type, current - amount);

        LOGGER.fine(String.format("Removed %d %s. New total: %d",
                amount, type.getName(), current - amount));

        return Result.success(amount);
    }

    /**
     * Sets the production rate for a resource.
     *
     * @param type  The resource type
     * @param value The production value
     */
    public void setProduction(ResourceType type, int value) {
        production.put(type, value);
        LOGGER.fine(String.format("Set %s production to %d",
                type.getName(), value));
    }

    /**
     * Sets the consumption rate for a resource.
     *
     * @param type  The resource type
     * @param value The consumption value
     */
    public void setConsumption(ResourceType type, int value) {
        consumption.put(type, value);
        LOGGER.fine(String.format("Set %s consumption to %d",
                type.getName(), value));
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

        return newCapacity;
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
            int before = resources.get(type);

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
                    resourceReport.append(type.getName()).append(": SHORTAGE (needed ").append(needed).append(")\n");
                } else {
                    resourceReport.append(type.getName()).append(": ").append(net).append("\n");
                }
            }
        }

        LOGGER.info(resourceReport.toString());
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
}