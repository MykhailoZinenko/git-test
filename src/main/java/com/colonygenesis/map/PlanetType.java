package com.colonygenesis.map;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

/**
 * Extended version of PlanetType with more variety and generation parameters.
 */
public enum PlanetType {
    TEMPERATE("Temperate", "Balanced resources and mild climate.",
            new TerrainDistribution()
                    .add(TerrainType.PLAINS, 0.35)
                    .add(TerrainType.FOREST, 0.25)
                    .add(TerrainType.MOUNTAINS, 0.15)
                    .add(TerrainType.WATER, 0.15)
                    .add(TerrainType.DESERT, 0.05)
                    .add(TerrainType.TUNDRA, 0.05)),

    DESERT("Desert", "Scarce water but abundant solar energy and minerals.",
            new TerrainDistribution()
                    .add(TerrainType.DESERT, 0.50)
                    .add(TerrainType.PLAINS, 0.20)
                    .add(TerrainType.MOUNTAINS, 0.20)
                    .add(TerrainType.WATER, 0.05)
                    .add(TerrainType.VOLCANIC, 0.05)),

    ARCTIC("Arctic", "Cold with frozen water reserves but rich in rare minerals.",
            new TerrainDistribution()
                    .add(TerrainType.TUNDRA, 0.45)
                    .add(TerrainType.MOUNTAINS, 0.25)
                    .add(TerrainType.WATER, 0.20)
                    .add(TerrainType.PLAINS, 0.10)),

    VOLCANIC("Volcanic", "Harsh conditions but plentiful energy and exotic compounds.",
            new TerrainDistribution()
                    .add(TerrainType.VOLCANIC, 0.30)
                    .add(TerrainType.MOUNTAINS, 0.30)
                    .add(TerrainType.DESERT, 0.25)
                    .add(TerrainType.PLAINS, 0.10)
                    .add(TerrainType.WATER, 0.05)),

    JUNGLE("Jungle", "Abundant plant life but challenging terrain.",
            new TerrainDistribution()
                    .add(TerrainType.FOREST, 0.50)
                    .add(TerrainType.PLAINS, 0.20)
                    .add(TerrainType.WATER, 0.20)
                    .add(TerrainType.MOUNTAINS, 0.10));

    private final String name;
    private final String description;
    private final TerrainDistribution terrainDistribution;

    PlanetType(String name, String description, TerrainDistribution terrainDistribution) {
        this.name = name;
        this.description = description;
        this.terrainDistribution = terrainDistribution;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TerrainDistribution getTerrainDistribution() {
        return terrainDistribution;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Helper class to define terrain distribution for different planet types.
     */
    public static class TerrainDistribution implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private final Map<TerrainType, Double> distribution = new EnumMap<>(TerrainType.class);

        public TerrainDistribution add(TerrainType type, double probability) {
            distribution.put(type, probability);
            return this;
        }

        public Map<TerrainType, Double> getDistribution() {
            return Collections.unmodifiableMap(distribution);
        }

        public TerrainType getRandomTerrain(Random random) {
            double value = random.nextDouble();
            double cumulativeProbability = 0.0;

            for (Map.Entry<TerrainType, Double> entry : distribution.entrySet()) {
                cumulativeProbability += entry.getValue();
                if (value <= cumulativeProbability) {
                    return entry.getKey();
                }
            }

            return TerrainType.PLAINS; // Default fallback
        }
    }
}