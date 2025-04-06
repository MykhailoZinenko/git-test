package com.colonygenesis.building;

/**
 * Enumeration of different building types in the game.
 */
public enum BuildingType {
    HABITATION("Habitation", "Provides living space for colonists"),
    PRODUCTION("Production", "Produces resources for the colony"),
    STORAGE("Storage", "Increases storage capacity for resources"),
    RESEARCH("Research", "Generates research points for technology advancement"),
    INFRASTRUCTURE("Infrastructure", "Supports the colony's basic needs"),
    DEFENSE("Defense", "Protects the colony from threats"),
    SPECIAL("Special", "Unique buildings with special abilities");

    private final String name;
    private final String description;

    BuildingType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}