package com.colonygenesis.map;

import com.colonygenesis.core.Game;
import com.colonygenesis.resource.ResourceType;
import com.colonygenesis.util.LoggerUtil;
import com.colonygenesis.util.Result;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Represents a planet with a hexagonal grid of tiles.
 */
public class Planet implements Serializable {
    private static final Logger LOGGER = LoggerUtil.getLogger(Planet.class);
    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private PlanetType type;
    private HexGrid grid;
    private ColonizationManager colonizationManager;

    public Planet(Game game, String name, PlanetType type, int mapSize) {
        this.name = name;
        this.type = type;

        MapGenerator generator = new MapGenerator();
        long seed = System.currentTimeMillis();
        this.grid = generator.generateMap(mapSize, mapSize, type, seed);

        this.colonizationManager = new ColonizationManager(game, grid);

        LOGGER.info("Created planet " + name + " of type " + type + " with grid size " + mapSize + "x" + mapSize);
    }

    public String getName() {
        return name;
    }

    public PlanetType getType() {
        return type;
    }

    public HexGrid getGrid() {
        return grid;
    }

    public ColonizationManager getColonizationManager() {
        return colonizationManager;
    }

    public Result<Boolean> colonizeTile(int x, int y) {
        return colonizationManager.colonizeTile(x, y);
    }

    public Map<ResourceType, Integer> getColonizationCost(int x, int y) {
        return colonizationManager.getColonizationCost(x, y);
    }

    /**
     * Resets the planet to a clean state.
     * This is useful when reusing a planet instance.
     *
     * @param game The game instance
     * @param name The new planet name
     * @param type The new planet type
     * @param mapSize The new map size
     */
    public void reset(Game game, String name, PlanetType type, int mapSize) {
        LOGGER.info("Resetting planet to: " + name + " of type " + type);

        this.name = name;
        this.type = type;

        MapGenerator generator = new MapGenerator();
        long seed = System.currentTimeMillis();
        this.grid = generator.generateMap(mapSize, mapSize, type, seed);

        this.colonizationManager = new ColonizationManager(game, grid);
    }

    /**
     * Diagnostic method to check for duplicate colonized tiles.
     * This helps identify potential state issues.
     *
     * @return The number of colonized tiles
     */
    public int countColonizedTiles() {
        int count = 0;
        HexGrid g = getGrid();
        for (int x = 0; x < g.getWidth(); x++) {
            for (int y = 0; y < g.getHeight(); y++) {
                Tile tile = g.getTileAt(x, y);
                if (tile != null && tile.isColonized()) {
                    count++;
                    LOGGER.fine("Found colonized tile at (" + x + "," + y + "): " + tile);
                }
            }
        }
        LOGGER.info("Total colonized tiles: " + count);
        return count;
    }
}