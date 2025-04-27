package com.colonygenesis.building;

import com.colonygenesis.core.Game;
import com.colonygenesis.map.Tile;
import com.colonygenesis.resource.ResourceType;
import com.colonygenesis.ui.events.BuildingEvents;
import com.colonygenesis.ui.events.EventBus;
import com.colonygenesis.ui.events.TileEvents;
import com.colonygenesis.util.LoggerUtil;
import com.colonygenesis.util.Result;

import java.io.IOException;
import java.io.ObjectInputStream;
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

    private Game game;
    private final List<AbstractBuilding> buildings;
    private final Map<Tile, AbstractBuilding> buildingsByTile;
    private final List<AbstractBuilding> buildingsUnderConstruction;

    private transient EventBus eventBus;

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

        processConstruction();

        Map<ResourceType, Integer> resourceChanges = new EnumMap<>(ResourceType.class);

        for (AbstractBuilding building : buildings) {
            if (building.isActive()) {
                // Special handling for habitation buildings with population growth
                if (building instanceof HabitationBuilding habitation && habitation.getPopulationGrowthRate() > 0) {
                    // Check if this building can support more population
                    int currentOccupied = habitation.getOccupied();
                    int buildingCapacity = habitation.getCapacity();
                    int growthRate = habitation.getPopulationGrowthRate();

                    if (currentOccupied < buildingCapacity) {
                        // Calculate how much this building can grow
                        int spaceAvailable = buildingCapacity - currentOccupied;
                        int actualGrowth = Math.min(growthRate, spaceAvailable);

                        // Check against total population capacity
                        int currentPopulation = game.getResourceManager().getResource(ResourceType.POPULATION);
                        int totalCapacity = game.getResourceManager().getCapacity(ResourceType.POPULATION);
                        int totalSpaceAvailable = totalCapacity - currentPopulation;

                        actualGrowth = Math.min(actualGrowth, totalSpaceAvailable);

                        if (actualGrowth > 0) {
                            // Update the building's occupied count - this will publish the event
                            habitation.setOccupied(currentOccupied + actualGrowth);

                            // Add the actual growth to resource changes
                            resourceChanges.merge(ResourceType.POPULATION, actualGrowth, Integer::sum);

                            LOGGER.info(String.format("Population grew by %d in %s. New occupancy: %d/%d",
                                    actualGrowth, building.getName(), habitation.getOccupied(), buildingCapacity));
                        } else {
                            LOGGER.info(String.format("No population growth in %s - at capacity", building.getName()));
                        }
                    }
                }

                // Handle other resources normally
                Map<ResourceType, Integer> buildingOutput = building.operate();
                for (Map.Entry<ResourceType, Integer> entry : buildingOutput.entrySet()) {
                    ResourceType type = entry.getKey();
                    int amount = entry.getValue();

                    // Skip population resource as it's handled above
                    if (type != ResourceType.POPULATION) {
                        resourceChanges.merge(type, amount, Integer::sum);
                    }
                }
            }
        }

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
            int previousProgress = building.getConstructionProgress();
            boolean wasCompleted = building.progressConstruction();
            int newProgress = building.getConstructionProgress();

            eventBus.publish(new BuildingEvents.BuildingConstructionProgressEvent(
                    building, previousProgress, newProgress));

            if (wasCompleted) {
                iterator.remove();
                LOGGER.info("Building completed: " + building.getName() + " at " + building.getLocation());

                eventBus.publish(new BuildingEvents.BuildingCompletedEvent(building));

                if (building instanceof HabitationBuilding habitation) {
                    game.getResourceManager().increaseCapacity(ResourceType.POPULATION, habitation.getCapacity());
                }

                if (building.getWorkersRequired() == 0) {
                    building.activate();
                }

                eventBus.publish(new TileEvents.TileUpdatedEvent(building.getLocation()));
            }
        }
    }


    /**
     * Applies resource changes to the game's resource manager.
     * Resets all production and consumption values before applying new values.
     *
     * @param resourceChanges Map of resource changes to apply
     */
    private void applyResourceChanges(Map<ResourceType, Integer> resourceChanges) {
        // Reset all production and consumption values to 0
        for (ResourceType type : ResourceType.values()) {
            game.getResourceManager().setProduction(type, 0);
            game.getResourceManager().setConsumption(type, 0);
        }

        // Apply the new production and consumption values
        for (Map.Entry<ResourceType, Integer> entry : resourceChanges.entrySet()) {
            ResourceType type = entry.getKey();
            int amount = entry.getValue();

            if (amount > 0) {
                game.getResourceManager().addProduction(type, amount);
            } else if (amount < 0) {
                game.getResourceManager().addConsumption(type, -amount);
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

        if (location == null) {
            return Result.failure("Invalid building location");
        }

        if (buildingsByTile.containsKey(location)) {
            return Result.failure("Tile already has a building");
        }

        if (!location.isColonized()) {
            return Result.failure("Can only build on colonized tiles");
        }

        Map<ResourceType, Integer> constructionCost = building.getConstructionCost();
        for (Map.Entry<ResourceType, Integer> entry : constructionCost.entrySet()) {
            ResourceType type = entry.getKey();
            int amount = entry.getValue();

            if (game.getResourceManager().getResource(type) < amount) {
                LOGGER.warning("Cannot afford building: " + building.getName());
                return Result.failure("Not enough " + type.getName() + " to construct this building");
            }
        }

        for (Map.Entry<ResourceType, Integer> entry : constructionCost.entrySet()) {
            ResourceType type = entry.getKey();
            int amount = entry.getValue();

            Result<Integer> result = game.getResourceManager().removeResource(type, amount);
            if (result.isFailure()) {
                LOGGER.warning("Failed to remove resources for building: " + result.getErrorMessage());
                return Result.failure(result.getErrorMessage());
            }
        }

        building.setResourceManager(game.getResourceManager());

        buildings.add(building);
        buildingsByTile.put(location, building);

        location.setBuilding(building);

        if (building.getRemainingConstructionTime() > 0) {
            buildingsUnderConstruction.add(building);
            LOGGER.info("Added " + building.getName() + " to construction queue");
            LOGGER.info("Construction time: " + building.getRemainingConstructionTime() + " turns");
        } else {
            if (building.getWorkersRequired() == 0) {
                building.activate();
            }

            if (building instanceof HabitationBuilding habitation) {
                game.getResourceManager().increaseCapacity(ResourceType.POPULATION, habitation.getCapacity());
            }

            LOGGER.info("Instantly constructed " + building.getName() + " at " + location);
        }

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

        if (building.getWorkersAssigned() > 0) {
            building.removeWorkers(building.getWorkersAssigned());
        }

        buildings.remove(building);
        buildingsByTile.remove(tile);
        buildingsUnderConstruction.remove(building);

        tile.setBuilding(null);

        LOGGER.info("Demolished " + building.getName() + " at " + tile);

        eventBus.publish(new TileEvents.TileUpdatedEvent(tile));

        return Result.success(true);
    }

    /**
     * Updates the resource manager reference for all buildings.
     * Should be called after loading a game.
     */
    public void updateResourceManagerReferences() {
        for (AbstractBuilding building : buildings) {
            building.setResourceManager(game.getResourceManager());
        }
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

    public void setGame(Game game) {
        this.game = game;
    }

    @Serial
    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();

        this.eventBus = EventBus.getInstance();

        updateResourceManagerReferences();

        LOGGER.info("BuildingManager deserialized and transient fields reinitialized");
    }
}