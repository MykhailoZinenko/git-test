package com.colonygenesis.map;

import com.colonygenesis.building.AbstractBuilding;
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
    private AbstractBuilding building;

    public Tile(int x, int y, TerrainType terrain) {
        this.x = x;
        this.y = y;
        this.terrain = terrain;
        this.colonized = false;
        this.revealed = false;
        this.building = null;
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

    /**
     * Gets the building on this tile.
     *
     * @return The building on this tile, or null if no building exists
     */
    public AbstractBuilding getBuilding() {
        return building;
    }

    /**
     * Sets the building on this tile.
     *
     * @param building The building to place on this tile
     */
    public void setBuilding(AbstractBuilding building) {
        this.building = building;
    }

    /**
     * Checks if this tile has a building.
     *
     * @return true if this tile has a building, false otherwise
     */
    public boolean hasBuilding() {
        return building != null;
    }

    /**
     * Checks if this tile has a building under construction.
     *
     * @return true if this tile has a building under construction, false otherwise
     */
    public boolean hasBuildingUnderConstruction() {
        return building != null && !building.isComplete();
    }

    /**
     * Checks if this tile has a completed building.
     *
     * @return true if this tile has a completed building, false otherwise
     */
    public boolean hasCompletedBuilding() {
        return building != null && building.isComplete();
    }

    @Override
    public String toString() {
        return "Tile[" + x + "," + y + "] " + terrain +
                (colonized ? " (Colonized)" : " (Not Colonized)") +
                (revealed ? " (Revealed)" : " (Not Revealed)") +
                (building != null ? " with " + building.getName() : "");
    }
}