package com.colonygenesis.map;

import com.colonygenesis.util.LoggerUtil;
import com.colonygenesis.util.SimplexNoise;

import java.io.Serial;
import java.io.Serializable;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Manages the process of generating terrain for planets.
 */
public class MapGenerator implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerUtil.getLogger(MapGenerator.class);

    /**
     * Generates a map based on planet type and size.
     */
    public HexGrid generateMap(int width, int height, PlanetType planetType, long seed) {
        LOGGER.info("Generating map for planet type: " + planetType + " with seed: " + seed);

        Random random = new Random(seed);
        HexGrid grid = new HexGrid(width, height);

        PlanetType.TerrainDistribution distribution = planetType.getTerrainDistribution();
        SimplexNoise elevationNoise = new SimplexNoise(random.nextLong());

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double noise1 = elevationNoise.noise(x * 0.1, y * 0.1);

                TerrainType terrainType;
                if (noise1 > 0.7) {
                    terrainType = TerrainType.MOUNTAINS;
                } else if (noise1 < -0.7) {
                    terrainType = TerrainType.WATER;
                } else {
                    terrainType = distribution.getRandomTerrain(random);
                }

                Tile tile = new Tile(x, y, terrainType);
                grid.setTileAt(x, y, tile);
            }
        }

        ensureHabitableCenter(grid, width, height);

        return grid;
    }

    /**
     * Makes sure the center of the map is habitable for starting colony.
     */
    private void ensureHabitableCenter(HexGrid grid, int width, int height) {
        int centerX = width / 2;
        int centerY = height / 2;

        for (int x = centerX - 2; x <= centerX + 2; x++) {
            for (int y = centerY - 2; y <= centerY + 2; y++) {
                Tile tile = grid.getTileAt(x, y);
                if (tile != null && !tile.isHabitable()) {
                    tile.setTerrainType(TerrainType.PLAINS);
                }
            }
        }

        Tile centerTile = grid.getTileAt(centerX, centerY);
        if (centerTile != null) {
            centerTile.setColonized(true);
            grid.revealTileAndNeighbors(centerX, centerY);
        }
    }
}