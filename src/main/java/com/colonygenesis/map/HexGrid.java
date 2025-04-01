package com.colonygenesis.map;

import com.colonygenesis.util.LoggerUtil;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Represents the hexagonal grid of tiles that makes up the planet surface.
 */
public class HexGrid implements Serializable {
    private static final Logger LOGGER = LoggerUtil.getLogger(HexGrid.class);
    @Serial
    private static final long serialVersionUID = 1L;

    private final int width;
    private final int height;
    private final Tile[][] tiles;

    public HexGrid(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new Tile[width][height];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Tile getTileAt(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return tiles[x][y];
        }
        return null;
    }

    public void setTileAt(int x, int y, Tile tile) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            tiles[x][y] = tile;
        }
    }

    public List<Tile> getNeighbors(Tile tile) {
        List<Tile> neighbors = new ArrayList<>();
        int x = tile.getX();
        int y = tile.getY();

        // Determine neighbors based on hex grid topology
        // For odd rows, the neighbors are shifted
        boolean isOddRow = (y % 2 == 1);

        int[][] directions;
        if (isOddRow) {
            directions = new int[][] {
                    {0, -1},   // North
                    {1, -1},   // Northeast
                    {1, 0},    // Southeast
                    {0, 1},    // South
                    {-1, 0},   // Southwest
                    {-1, -1}   // Northwest
            };
        } else {
            directions = new int[][] {
                    {0, -1},   // North
                    {1, 0},    // Northeast
                    {1, 1},    // Southeast
                    {0, 1},    // South
                    {-1, 1},   // Southwest
                    {-1, 0}    // Northwest
            };
        }

        for (int[] dir : directions) {
            int nx = x + dir[0];
            int ny = y + dir[1];

            Tile neighbor = getTileAt(nx, ny);
            if (neighbor != null) {
                neighbors.add(neighbor);
            }
        }

        return neighbors;
    }

    public boolean canColonize(Tile tile) {
        if (tile == null || tile.isColonized() || !tile.isHabitable()) {
            return false;
        }

        for (Tile neighbor : getNeighbors(tile)) {
            if (neighbor.isColonized()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Reveals the specified tile and its neighbors.
     */
    public void revealTile(int x, int y) {
        Tile tile = getTileAt(x, y);
        if (tile != null) {
            tile.setRevealed(true);
        }
    }

    /**
     * Reveals the specified tile and its neighbors.
     */
    public void revealTileAndNeighbors(int x, int y) {
        Tile tile = getTileAt(x, y);
        if (tile == null) return;

        tile.setRevealed(true);

        for (Tile neighbor : getNeighbors(tile)) {
            neighbor.setRevealed(true);
        }
    }
}