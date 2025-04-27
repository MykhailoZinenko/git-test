package com.colonygenesis.victory;

/**
 * Enumeration of victory types in the game.
 */
public enum VictoryType {
    SCIENTIFIC("Scientific Victory", "Research key technologies to achieve technological mastery"),
    INDUSTRIAL("Industrial Victory", "Build massive structures and master industrial production"),
    HARMONY("Harmony Victory", "Achieve perfect balance with the planet's ecosystem"),
    EXPANSIONIST("Expansionist Victory", "Grow your colony to dominate the planet");

    private final String name;
    private final String description;

    VictoryType(String name, String description) {
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