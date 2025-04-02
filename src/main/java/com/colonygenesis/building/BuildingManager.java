package com.colonygenesis.building;

import com.colonygenesis.core.Game;
import com.colonygenesis.map.Tile;
import com.colonygenesis.resource.ResourceType;
import com.colonygenesis.ui.events.BuildingEvents;
import com.colonygenesis.ui.events.EventBus;
import com.colonygenesis.ui.events.TileEvents;
import com.colonygenesis.util.LoggerUtil;
import com.colonygenesis.util.Result;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

/**
 * Manages all buildings in the game.
 * Handles construction, operation, and resource management for buildings.
 */
public class BuildingManager implements Serializable {
    private static final Logger LOGGER = LoggerUtil.getLogger(BuildingManager.class);
    @Serial
    private static final long serialVersionUID = 1L;

    private final Game game;
    private final List<AbstractBuilding> buildings;
    private final Map<Tile, AbstractBuilding> buildingsByTile;
    private final List<AbstractBuilding> buildingsUnderConstruction;

    // Event bus for publishing events
    private final transient EventBus eventBus;

    /**
     * Constructs a new building manager for the specified game.
     *
     * @param game The game
     */
    public BuildingManager(Game game) {
        this.game = game;
        this.buildings = new ArrayList<>();
        this.buildingsByTile = new HashMap<>();
        this.buildingsUnderConstruction = new ArrayList<>();
        this.eventBus = EventBus.getInstance();

        LOGGER.info("BuildingManager initialized");
    }

    /**
     * Processes buildings for the current turn.
     * Updates construction progress and operates all active buildings.
     */
    public void processTurn() {
        LOGGER.fine("Processing buildings for turn " + game.getCurrentTurn());

        // Process buildings under construction
        processConstruction();

        // Process operational buildings
        Map<ResourceType, Integer> resourceChanges = new EnumMap<>(ResourceType.class);

        for (AbstractBuilding building : buildings) {
            if (building.isActive()) {
                Map<ResourceType, Integer> buildingOutput = building.operate();

                // Merge building output into resource changes
                for (Map.Entry<ResourceType, Integer> entry : buildingOutput.entrySet()) {
                    ResourceType type = entry.getKey();
                    int amount = entry.getValue();

                    resourceChanges.merge(type, amount, Integer::sum);
                }
            }
        }

        // Apply resource changes to the game
        applyResourceChanges(resourceChanges);

        LOGGER.fine("Finished processing buildings for turn " + game.getCurrentTurn());
    }

    /**
     * Processes buildings under construction.
     * Advances construction progress and activates completed buildings.
     */
    private void processConstruction() {
        Iterator<AbstractBuilding> iterator = buildingsUnderConstruction.iterator();

        while (iterator.hasNext()) {
            AbstractBuilding building = iterator.next();
            boolean wasCompleted = building.progressConstruction();

            if (wasCompleted) {
                iterator.remove();
                LOGGER.info("Building completed: " + building.getName() + " at " + building.getLocation());

                // If building doesn't require workers, activate it immediately
                if (building.getWorkersRequired() == 0) {
                    building.activate();
                }

                // Publish tile updated event
                eventBus.publish(new TileEvents.TileUpdatedEvent(building.getLocation()));
            }
        }
    }

    /**
     * Applies resource changes to the game's resource manager.
     *
     * @param resourceChanges Map of resource changes to apply
     */
    private void applyResourceChanges(Map<ResourceType, Integer> resourceChanges) {
        for (Map.Entry<ResourceType, Integer> entry : resourceChanges.entrySet()) {
            ResourceType type = entry.getKey();
            int amount = entry.getValue();

            if (amount > 0) {
                game.getResourceManager().addResource(type, amount);
            } else if (amount < 0) {
                // For consumption, we need to convert the negative amount to positive
                game.getResourceManager().removeResource(type, -amount);
            }
        }
    }

    /**
     * Starts construction of a building on the specified tile.
     *
     * @param building The building to construct
     * @return A Result indicating success or failure
     */
    public Result<AbstractBuilding> constructBuilding(AbstractBuilding building) {
        Tile location = building.getLocation();

        // Check if the tile is valid for construction
        if (location == null) {
            return Result.failure("Invalid building location");
        }

        // Check if the tile is already occupied
        if (buildingsByTile.containsKey(location)) {
            return Result.failure("Tile already has a building");
        }

        // Check if the tile is colonized
        if (!location.isColonized()) {
            return Result.failure("Can only build on colonized tiles");
        }

        // Check if we have enough resources
        Map<ResourceType, Integer> constructionCost = building.getConstructionCost();
        for (Map.Entry<ResourceType, Integer> entry : constructionCost.entrySet()) {
            ResourceType type = entry.getKey();
            int amount = entry.getValue();

            if (game.getResourceManager().getResource(type) < amount) {
                LOGGER.warning("Cannot afford building: " + building.getName());
                return Result.failure("Not enough " + type.getName() + " to construct this building");
            }
        }

        // Deduct resources
        for (Map.Entry<ResourceType, Integer> entry : constructionCost.entrySet()) {
            ResourceType type = entry.getKey();
            int amount = entry.getValue();

            Result<Integer> result = game.getResourceManager().removeResource(type, amount);
            if (result.isFailure()) {
                LOGGER.warning("Failed to remove resources for building: " + result.getErrorMessage());
                return Result.failure(result.getErrorMessage());
            }
        }

        // Add the building to our collections
        buildings.add(building);
        buildingsByTile.put(location, building);

        // Set the building on the tile
        location.setBuilding(building);

        // If the building has construction time, add it to buildings under construction
        if (building.getRemainingConstructionTime() > 0) {
            buildingsUnderConstruction.add(building);
            LOGGER.info("Added " + building.getName() + " to construction queue");
            LOGGER.info("Construction time: " + building.getRemainingConstructionTime() + " turns");
        } else {
            // Instant construction - if building doesn't require workers, activate it immediately
            if (building.getWorkersRequired() == 0) {
                building.activate();
            }
            LOGGER.info("Instantly constructed " + building.getName() + " at " + location);
        }

        // Publish events
        eventBus.publish(new BuildingEvents.BuildingPlacedEvent(building, location));
        eventBus.publish(new TileEvents.TileUpdatedEvent(location));

        return Result.success(building);
    }

    /**
     * Demolishes a building at the specified tile.
     *
     * @param tile The tile with the building to demolish
     * @return A Result indicating success or failure
     */
    public Result<Boolean> demolishBuilding(Tile tile) {
        AbstractBuilding building = buildingsByTile.get(tile);

        if (building == null) {
            return Result.failure("No building at the specified location");
        }

        // Remove the building from our collections
        buildings.remove(building);
        buildingsByTile.remove(tile);
        buildingsUnderConstruction.remove(building);

        // Clear the building reference from the tile
        tile.setBuilding(null);

        LOGGER.info("Demolished " + building.getName() + " at " + tile);

        // Publish events
        eventBus.publish(new TileEvents.TileUpdatedEvent(tile));

        return Result.success(true);
    }

    /**
     * Gets all buildings in the game.
     *
     * @return A list of all buildings
     */
    public List<AbstractBuilding> getAllBuildings() {
        return new ArrayList<>(buildings);
    }

    /**
     * Gets buildings of a specific type.
     *
     * @param type The building type to filter by
     * @return A list of buildings of the specified type
     */
    public List<AbstractBuilding> getBuildingsByType(BuildingType type) {
        return buildings.stream()
                .filter(b -> b.getBuildingType() == type)
                .toList();
    }

    /**
     * Gets the building at the specified tile.
     *
     * @param tile The tile to check
     * @return The building at the tile, or null if none exists
     */
    public AbstractBuilding getBuildingAt(Tile tile) {
        return buildingsByTile.get(tile);
    }

    /**
     * Gets all buildings currently under construction.
     *
     * @return A list of buildings under construction
     */
    public List<AbstractBuilding> getBuildingsUnderConstruction() {
        return new ArrayList<>(buildingsUnderConstruction);
    }

    /**
     * Gets the total number of buildings.
     *
     * @return The total number of buildings
     */
    public int getBuildingCount() {
        return buildings.size();
    }

    /**
     * Gets the total number of active buildings.
     *
     * @return The total number of active buildings
     */
    public int getActiveBuildings() {
        return (int) buildings.stream().filter(AbstractBuilding::isActive).count();
    }

    /**
     * Gets the total number of buildings under construction.
     *
     * @return The total number of buildings under construction
     */
    public int getBuildingsUnderConstructionCount() {
        return buildingsUnderConstruction.size();
    }
}