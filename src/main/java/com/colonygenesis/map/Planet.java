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

    private final String name;
    private final PlanetType type;
    private final HexGrid grid;
    private final ColonizationManager colonizationManager;

    public Planet(Game game, String name, PlanetType type, int mapSize) {
        this.name = name;
        this.type = type;

        MapGenerator generator = new MapGenerator();
        long seed = System.currentTimeMillis();
        this.grid = generator.generateMap(mapSize, mapSize, type, seed);

        this.colonizationManager = new ColonizationManager(game, grid);

        LOGGER.info("Created planet " + name + " of type " + type);
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
}