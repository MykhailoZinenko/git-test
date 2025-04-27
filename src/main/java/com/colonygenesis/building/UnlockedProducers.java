package com.colonygenesis.building;

import com.colonygenesis.core.Game;
import com.colonygenesis.map.Tile;
import com.colonygenesis.resource.ResourceType;

import java.io.Serial;
import java.util.Map;

/**
 * Advanced production buildings unlocked by research.
 */
public class UnlockedProducers extends ProductionBuilding {
    @Serial
    private static final long serialVersionUID = 1L;

    private final UnlockedProducerType producerType;

    /**
     * Constructs a new unlocked producer building.
     *
     * @param producerType The type of unlocked producer
     * @param location The tile where the building is located
     */
    public UnlockedProducers(UnlockedProducerType producerType, Tile location, Game game) {
        super(
                producerType.getName(),
                producerType.getDescription(),
                location,
                producerType.getConstructionTime(),
                producerType.getWorkersRequired(),
                producerType.getOutputType(),
                producerType.getBaseOutput(),
                game
        );

        this.producerType = producerType;

        constructionCost.putAll(producerType.getConstructionCost());
        maintenanceCost.putAll(producerType.getMaintenanceCost());
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
    protected void calculateResourceConsumption(Map<ResourceType, Integer> output) {

    }

    /**
     * Gets the type of unlocked producer.
     *
     * @return The producer type
     */
    public UnlockedProducerType getProducerType() {
        return producerType;
    }

    /**
     * Enum for different types of unlocked producers.
     */
    public enum UnlockedProducerType {
        HYDROPONICS_FARM("Basic Hydroponics Farm", "Advanced food production facility",
                ResourceType.FOOD, 15, 4, 6, "hydroponics_farm",
                Map.of(ResourceType.MATERIALS, 100, ResourceType.ENERGY, 50),
                Map.of(ResourceType.WATER, 3, ResourceType.ENERGY, 3)),

        ATMOSPHERE_PROCESSOR("Atmosphere Processor", "Maintains optimal atmospheric conditions",
                ResourceType.WATER, 10, 3, 8, "atmosphere_processor",
                Map.of(ResourceType.MATERIALS, 150, ResourceType.ENERGY, 80),
                Map.of(ResourceType.ENERGY, 5)),

        VERTICAL_FARM("Vertical Farm", "Highly efficient multi-level farming system",
                ResourceType.FOOD, 30, 6, 10, "vertical_farm",
                Map.of(ResourceType.MATERIALS, 250, ResourceType.ENERGY, 100),
                Map.of(ResourceType.WATER, 5, ResourceType.ENERGY, 6)),

        DEEP_MINE("Deep Mine", "Extracts rare minerals from deep underground",
                ResourceType.RARE_MINERALS, 5, 5, 8, "deep_mine",
                Map.of(ResourceType.MATERIALS, 200, ResourceType.ENERGY, 100),
                Map.of(ResourceType.ENERGY, 8)),

        FUSION_REACTOR("Fusion Reactor", "Advanced power generation facility",
                ResourceType.ENERGY, 40, 6, 12, "fusion_reactor",
                Map.of(ResourceType.MATERIALS, 300, ResourceType.ENERGY, 100, ResourceType.RARE_MINERALS, 50),
                Map.of(ResourceType.WATER, 3)),

        ORBITAL_PLATFORM("Orbital Platform", "Space-based manufacturing facility",
                ResourceType.MATERIALS, 25, 8, 15, "orbital_platform",
                Map.of(ResourceType.MATERIALS, 500, ResourceType.ENERGY, 300, ResourceType.RARE_MINERALS, 100),
                Map.of(ResourceType.ENERGY, 10)),

        QUANTUM_LAB("Quantum Lab", "Cutting-edge research facility",
                ResourceType.RESEARCH, 20, 5, 10, "quantum_lab",
                Map.of(ResourceType.MATERIALS, 200, ResourceType.ENERGY, 100, ResourceType.RARE_MINERALS, 30),
                Map.of(ResourceType.ENERGY, 8)),

        XENOBIOLOGY_LAB("Xenobiology Lab", "Studies alien life forms and compounds",
                ResourceType.ALIEN_COMPOUNDS, 8, 4, 12, "xenobiology_lab",
                Map.of(ResourceType.MATERIALS, 250, ResourceType.ENERGY, 120, ResourceType.RESEARCH, 100),
                Map.of(ResourceType.ENERGY, 6, ResourceType.RESEARCH, 5)),

        ALIEN_MEGASTRUCTURE("Alien Megastructure", "Mysterious alien technology",
                ResourceType.ALIEN_COMPOUNDS, 20, 10, 20, "alien_megastructure",
                Map.of(ResourceType.MATERIALS, 1000, ResourceType.ENERGY, 500, ResourceType.ALIEN_COMPOUNDS, 200),
                Map.of(ResourceType.ENERGY, 20));

        private final String name;
        private final String description;
        private final ResourceType outputType;
        private final int baseOutput;
        private final int workersRequired;
        private final int constructionTime;
        private final String unlockId;
        private final Map<ResourceType, Integer> constructionCost;
        private final Map<ResourceType, Integer> maintenanceCost;

        UnlockedProducerType(String name, String description,
                             ResourceType outputType, int baseOutput,
                             int workersRequired, int constructionTime,
                             String unlockId,
                             Map<ResourceType, Integer> constructionCost,
                             Map<ResourceType, Integer> maintenanceCost) {
            this.name = name;
            this.description = description;
            this.outputType = outputType;
            this.baseOutput = baseOutput;
            this.workersRequired = workersRequired;
            this.constructionTime = constructionTime;
            this.unlockId = unlockId;
            this.constructionCost = constructionCost;
            this.maintenanceCost = maintenanceCost;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
        public ResourceType getOutputType() { return outputType; }
        public int getBaseOutput() { return baseOutput; }
        public int getWorkersRequired() { return workersRequired; }
        public int getConstructionTime() { return constructionTime; }
        public String getUnlockId() { return unlockId; }
        public Map<ResourceType, Integer> getConstructionCost() { return constructionCost; }
        public Map<ResourceType, Integer> getMaintenanceCost() { return maintenanceCost; }
    }
}