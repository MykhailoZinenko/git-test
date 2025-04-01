package com.colonygenesis.map;

import com.colonygenesis.resource.ResourceType;
import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a tile on the game map with a specific terrain type.
 * Tiles can be colonized and have buildings constructed on them.
 */
public class Tile implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int x;
    private final int y;
    private TerrainType terrain;
    private boolean colonized;
    private boolean revealed;

    public Tile(int x, int y, TerrainType terrain) {
        this.x = x;
        this.y = y;
        this.terrain = terrain;
        this.colonized = false;
        this.revealed = false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public TerrainType getTerrainType() {
        return terrain;
    }

    public void setTerrainType(TerrainType terrain) {
        this.terrain = terrain;
    }

    public boolean isColonized() {
        return colonized;
    }

    public void setColonized(boolean colonized) {
        this.colonized = colonized;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }

    public double getResourceYield(ResourceType resourceType) {
        return terrain.getResourceModifier(resourceType);
    }

    public boolean isHabitable() {
        return terrain != TerrainType.WATER && terrain != TerrainType.VOLCANIC;
    }

    @Override
    public String toString() {
        return "Tile[" + x + "," + y + "] " + terrain + (colonized ? " (Colonized)" : "");
    }
}