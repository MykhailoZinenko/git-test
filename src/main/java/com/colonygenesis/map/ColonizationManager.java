package com.colonygenesis.map;

import com.colonygenesis.core.Game;
import com.colonygenesis.resource.ResourceType;
import com.colonygenesis.ui.events.EventBus;
import com.colonygenesis.ui.events.TileEvents;
import com.colonygenesis.util.LoggerUtil;
import com.colonygenesis.util.Result;

import java.io.Serial;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Manages the colonization of tiles on the planet.
 */
public class ColonizationManager implements Serializable {
    private static final Logger LOGGER = LoggerUtil.getLogger(ColonizationManager.class);
    @Serial
    private static final long serialVersionUID = 1L;

    private final Game game;
    private final HexGrid grid;
    private final Map<TerrainType, Map<ResourceType, Integer>> colonizationCosts;

    public ColonizationManager(Game game, HexGrid grid) {
        this.game = game;
        this.grid = grid;
        this.colonizationCosts = initializeColonizationCosts();
    }

    /**
     * Sets up the resource costs for colonizing different terrain types.
     */
    private Map<TerrainType, Map<ResourceType, Integer>> initializeColonizationCosts() {
        Map<TerrainType, Map<ResourceType, Integer>> costs = new EnumMap<>(TerrainType.class);

        costs.put(TerrainType.PLAINS, Map.of(
                ResourceType.MATERIALS, 50,
                ResourceType.ENERGY, 20
        ));

        costs.put(TerrainType.DESERT, Map.of(
                ResourceType.MATERIALS, 50,
                ResourceType.ENERGY, 30,
                ResourceType.WATER, 20
        ));

        costs.put(TerrainType.MOUNTAINS, Map.of(
                ResourceType.MATERIALS, 100,
                ResourceType.ENERGY, 30
        ));

        costs.put(TerrainType.FOREST, Map.of(
                ResourceType.MATERIALS, 70,
                ResourceType.ENERGY, 30
        ));

        costs.put(TerrainType.TUNDRA, Map.of(
                ResourceType.MATERIALS, 70,
                ResourceType.ENERGY, 50
        ));

        costs.put(TerrainType.VOLCANIC, Map.of(
                ResourceType.MATERIALS, 150,
                ResourceType.ENERGY, 80,
                ResourceType.RARE_MINERALS, 10
        ));

        costs.put(TerrainType.WATER, Map.of(
                ResourceType.MATERIALS, 150,
                ResourceType.ENERGY, 50
        ));

        return costs;
    }

    /**
     * Gets the colonization cost for a specific tile.
     */
    public Map<ResourceType, Integer> getColonizationCost(int x, int y) {
        Tile tile = grid.getTileAt(x, y);
        if (tile == null) {
            return Map.of();
        }

        return colonizationCosts.getOrDefault(tile.getTerrainType(), Map.of());
    }

    /**
     * Checks if a tile can be colonized.
     */
    public Result<Boolean> canColonizeTile(int x, int y) {
        Tile tile = grid.getTileAt(x, y);

        if (tile == null) {
            return Result.failure("Invalid tile coordinates");
        }

        LOGGER.fine("Checking if tile at (" + x + "," + y + ") can be colonized. Current state: " +
                "isColonized=" + tile.isColonized() +
                ", isHabitable=" + tile.isHabitable() +
                ", canColonize=" + grid.canColonize(tile));

        if (tile.isColonized()) {
            return Result.failure("Tile is already colonized");
        }

        if (!tile.isHabitable()) {
            return Result.failure("Tile terrain is not habitable");
        }

        if (!grid.canColonize(tile)) {
            return Result.failure("Tile must be adjacent to an already colonized tile");
        }

        Map<ResourceType, Integer> cost = getColonizationCost(x, y);
        for (Map.Entry<ResourceType, Integer> entry : cost.entrySet()) {
            ResourceType type = entry.getKey();
            int requiredAmount = entry.getValue();

            if (game.getResourceManager().getResource(type) < requiredAmount) {
                return Result.failure("Not enough " + type.getName() + " to colonize this tile");
            }
        }

        return Result.success(true);
    }

    /**
     * Attempts to colonize a tile, consuming the required resources.
     */
    public Result<Boolean> colonizeTile(int x, int y) {
        LOGGER.info("Attempting to colonize tile at (" + x + "," + y + ")");

        Result<Boolean> canColonize = canColonizeTile(x, y);
        if (canColonize.isFailure()) {
            LOGGER.warning("Cannot colonize tile at (" + x + "," + y + "): " + canColonize.getErrorMessage());
            return canColonize;
        }

        Tile tile = grid.getTileAt(x, y);
        if (tile == null) {
            return Result.failure("Invalid tile coordinates");
        }

        LOGGER.info("Pre-colonization state of tile (" + x + "," + y + "): " + tile);

        Map<ResourceType, Integer> cost = getColonizationCost(x, y);
        for (Map.Entry<ResourceType, Integer> entry : cost.entrySet()) {
            ResourceType type = entry.getKey();
            int amount = entry.getValue();

            Result<Integer> result = game.getResourceManager().removeResource(type, amount);
            if (result.isFailure()) {
                LOGGER.warning("Failed to remove resources for colonization: " + result.getErrorMessage());
                return Result.failure(result.getErrorMessage());
            }
        }

        tile.setColonized(true);
        grid.revealTileAndNeighbors(x, y);

        LOGGER.info("Colonized tile at (" + x + "," + y + "): " + tile);

        EventBus.getInstance().publish(new TileEvents.TileUpdatedEvent(tile));
        EventBus.getInstance().publish(new TileEvents.RefreshMapEvent());

        return Result.success(true);
    }
}