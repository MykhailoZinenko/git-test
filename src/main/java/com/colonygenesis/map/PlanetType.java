package com.colonygenesis.map;

public enum PlanetType {
    TEMPERATE("Temperate", "Balanced resources and mild climate.");

    private final String name;
    private final String description;

    PlanetType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name;
    }
}