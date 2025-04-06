package com.colonygenesis.building;

import com.colonygenesis.map.Tile;
import com.colonygenesis.map.TerrainType;
import com.colonygenesis.resource.ResourceType;

import java.io.Serial;
import java.util.Map;

/**
 * A building that produces basic resources like food, materials, or energy.
 * Third layer in the building hierarchy.
 */
public class ResourceProducer extends ProductionBuilding {
    @Serial
    private static final long serialVersionUID = 1L;

    private final ResourceProducerType producerType;

    /**
     * Constructs a new resource producer building.
     *
     * @param producerType The type of resource producer
     * @param location The tile where the building is located
     */
    public ResourceProducer(ResourceProducerType producerType, Tile location) {
        super(
                producerType.getName(),
                producerType.getDescription(),
                location,
                producerType.getConstructionTime(),
                producerType.getWorkersRequired(),
                producerType.getOutputType(),
                producerType.getBaseOutput()
        );

        this.producerType = producerType;

        constructionCost.putAll(producerType.getConstructionCost());

        maintenanceCost.putAll(producerType.getMaintenanceCost());

        if (location != null) {
            TerrainType terrain = location.getTerrainType();

            if (producerType == ResourceProducerType.FARM && terrain == TerrainType.PLAINS) {
                addProductionModifier("plains_bonus", 1.2f);
            } else if (producerType == ResourceProducerType.MINE && terrain == TerrainType.MOUNTAINS) {
                addProductionModifier("mountains_bonus", 1.5f);
            } else if (producerType == ResourceProducerType.SOLAR_ARRAY && terrain == TerrainType.DESERT) {
                addProductionModifier("desert_bonus", 1.3f);
            }
        }
    }

    @Override
    protected void initializeConstructionCost() {
        constructionCost.putAll(producerType.getConstructionCost());
    }

    @Override
    protected void initializeMaintenanceCost() {
        maintenanceCost.putAll(producerType.getMaintenanceCost());
    }

    @Override
    public Map<ResourceType, Integer> operate() {
        Map<ResourceType, Integer> output = super.operate();

        if (isActive()) {
            if (producerType == ResourceProducerType.FARM) {
                output.put(ResourceType.WATER, 1);
            }
            else if (producerType == ResourceProducerType.MINE) {
                if (Math.random() < 0.1) {  // 10% chance
                    output.put(ResourceType.RARE_MINERALS, 1);
                }
            }
        }

        return output;
    }

    /**
     * Gets the type of resource producer.
     *
     * @return The resource producer type
     */
    public ResourceProducerType getProducerType() {
        return producerType;
    }

    /**
     * Enum for different types of resource producers.
     */
    public enum ResourceProducerType {
        FARM("Farm", "Produces food for your colony",
                ResourceType.FOOD, 10, 3, 5,
                Map.of(ResourceType.MATERIALS, 50, ResourceType.ENERGY, 20),
                Map.of(ResourceType.WATER, 2, ResourceType.ENERGY, 1)),

        MINE("Mine", "Extracts materials from the ground",
                ResourceType.MATERIALS, 8, 4, 6,
                Map.of(ResourceType.MATERIALS, 60, ResourceType.ENERGY, 30),
                Map.of(ResourceType.ENERGY, 3)),

        SOLAR_ARRAY("Solar Array", "Generates energy from sunlight",
                ResourceType.ENERGY, 12, 2, 4,
                Map.of(ResourceType.MATERIALS, 80, ResourceType.ENERGY, 10),
                Map.of(ResourceType.MATERIALS, 1)),

        WATER_EXTRACTOR("Water Extractor", "Extracts and purifies water",
                ResourceType.WATER, 8, 3, 5,
                Map.of(ResourceType.MATERIALS, 70, ResourceType.ENERGY, 40),
                Map.of(ResourceType.ENERGY, 4));

        private final String name;
        private final String description;
        private final ResourceType outputType;
        private final int baseOutput;
        private final int workersRequired;
        private final int constructionTime;
        private final Map<ResourceType, Integer> constructionCost;
        private final Map<ResourceType, Integer> maintenanceCost;

        ResourceProducerType(String name, String description,
                             ResourceType outputType, int baseOutput,
                             int workersRequired, int constructionTime,
                             Map<ResourceType, Integer> constructionCost,
                             Map<ResourceType, Integer> maintenanceCost) {
            this.name = name;
            this.description = description;
            this.outputType = outputType;
            this.baseOutput = baseOutput;
            this.workersRequired = workersRequired;
            this.constructionTime = constructionTime;
            this.constructionCost = constructionCost;
            this.maintenanceCost = maintenanceCost;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public ResourceType getOutputType() {
            return outputType;
        }

        public int getBaseOutput() {
            return baseOutput;
        }

        public int getWorkersRequired() {
            return workersRequired;
        }

        public int getConstructionTime() {
            return constructionTime;
        }

        public Map<ResourceType, Integer> getConstructionCost() {
            return constructionCost;
        }

        public Map<ResourceType, Integer> getMaintenanceCost() {
            return maintenanceCost;
        }
    }
}