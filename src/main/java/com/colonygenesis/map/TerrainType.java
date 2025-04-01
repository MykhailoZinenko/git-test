package com.colonygenesis.map;

import com.colonygenesis.resource.ResourceType;
import javafx.scene.paint.Color;

import java.util.Map;

/**
 * Enumeration of different terrain types that can appear on tiles.
 * Each type has unique properties and resource modifiers.
 */
public enum TerrainType {
    PLAINS("Plains", "Flat areas suitable for most buildings.", Color.LIGHTGREEN,
            Map.of(ResourceType.FOOD, 1.2, ResourceType.ENERGY, 1.0, ResourceType.MATERIALS, 0.8)),
    DESERT("Desert", "Dry areas with abundant solar potential.", Color.KHAKI,
            Map.of(ResourceType.FOOD, 0.5, ResourceType.ENERGY, 1.5, ResourceType.MATERIALS, 0.7)),
    MOUNTAINS("Mountains", "Elevated terrain rich in minerals.", Color.GRAY,
            Map.of(ResourceType.FOOD, 0.3, ResourceType.ENERGY, 0.8, ResourceType.MATERIALS, 1.5, ResourceType.RARE_MINERALS, 1.3)),
    WATER("Water", "Bodies of liquid suitable for water extraction.", Color.LIGHTSKYBLUE,
            Map.of(ResourceType.FOOD, 0.8, ResourceType.WATER, 2.0, ResourceType.ENERGY, 0.5)),
    FOREST("Forest", "Wooded areas with diverse resources.", Color.DARKGREEN,
            Map.of(ResourceType.FOOD, 1.5, ResourceType.MATERIALS, 1.3, ResourceType.ENERGY, 0.7)),
    TUNDRA("Tundra", "Cold regions with frozen water reserves.", Color.LIGHTCYAN,
            Map.of(ResourceType.FOOD, 0.4, ResourceType.WATER, 1.2, ResourceType.MATERIALS, 0.8, ResourceType.RARE_MINERALS, 1.1)),
    VOLCANIC("Volcanic", "Active geological areas rich in exotic compounds.", Color.DARKRED,
            Map.of(ResourceType.FOOD, 0.2, ResourceType.ENERGY, 1.8, ResourceType.MATERIALS, 0.6, ResourceType.ALIEN_COMPOUNDS, 1.4));

    private final String name;
    private final String description;
    private final Color color;
    private final Map<ResourceType, Double> resourceModifiers;

    TerrainType(String name, String description, Color color, Map<ResourceType, Double> resourceModifiers) {
        this.name = name;
        this.description = description;
        this.color = color;
        this.resourceModifiers = resourceModifiers;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Color getColor() {
        return color;
    }

    public double getMovementCost() {
        return switch(this) {
            case PLAINS -> 1.0;
            case DESERT -> 1.2;
            case MOUNTAINS -> 2.0;
            case WATER -> 3.0;
            case FOREST -> 1.5;
            case TUNDRA -> 1.3;
            case VOLCANIC -> 1.8;
        };
    }

    public double getBuildingModifier() {
        return switch(this) {
            case PLAINS -> 1.0;
            case DESERT -> 1.1;
            case MOUNTAINS -> 1.5;
            case WATER -> 2.0;
            case FOREST -> 1.2;
            case TUNDRA -> 1.3;
            case VOLCANIC -> 1.6;
        };
    }

    public double getResourceModifier(ResourceType resourceType) {
        return resourceModifiers.getOrDefault(resourceType, 1.0);
    }
}