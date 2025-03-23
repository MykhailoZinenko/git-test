package com.colonygenesis.map;

/**
 * Enumeration of planet types.
 * Each type has unique characteristics that affect gameplay.
 */
public enum PlanetType {
    /**
     * Temperate planet with balanced resources and mild climate.
     */
    TEMPERATE("Temperate", "Balanced resources and mild climate.");

    private final String name;
    private final String description;

    /**
     * Constructs a planet type with a name and description.
     *
     * @param name The name of the planet type
     * @param description The description of the planet type
     */
    PlanetType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Gets the name of the planet type.
     *
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the planet type.
     *
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name;
    }
}