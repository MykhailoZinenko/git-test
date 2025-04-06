package com.colonygenesis.building;

import com.colonygenesis.map.Tile;
import com.colonygenesis.resource.ResourceType;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * An advanced building that can convert one resource to another or produce advanced resources.
 * Third layer in the building hierarchy.
 */
public class AdvancedProducer extends ProductionBuilding {
    @Serial
    private static final long serialVersionUID = 1L;

    private final AdvancedProducerType producerType;
    private final List<ResourceType> inputTypes;
    private final List<Integer> inputAmounts;

    /**
     * Constructs a new advanced producer building.
     *
     * @param producerType The type of advanced producer
     * @param location The tile where the building is located
     */
    public AdvancedProducer(AdvancedProducerType producerType, Tile location) {
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
        this.inputTypes = new ArrayList<>(producerType.getInputTypes());
        this.inputAmounts = new ArrayList<>(producerType.getInputAmounts());

        for (Map.Entry<ResourceType, Integer> entry : producerType.getConstructionCost().entrySet()) {
            constructionCost.put(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<ResourceType, Integer> entry : producerType.getMaintenanceCost().entrySet()) {
            maintenanceCost.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    protected void initializeConstructionCost() {
        for (Map.Entry<ResourceType, Integer> entry : producerType.getConstructionCost().entrySet()) {
            constructionCost.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    protected void initializeMaintenanceCost() {
        for (Map.Entry<ResourceType, Integer> entry : producerType.getMaintenanceCost().entrySet()) {
            maintenanceCost.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Map<ResourceType, Integer> operate() {
        Map<ResourceType, Integer> resourceChanges = super.operate();

        if (isActive()) {
            for (int i = 0; i < inputTypes.size(); i++) {
                ResourceType inputType = inputTypes.get(i);
                int inputAmount = inputAmounts.get(i);

                resourceChanges.put(inputType, -inputAmount);
            }

            if (producerType == AdvancedProducerType.RESEARCH_LAB) {
                if (location != null && location.getTerrainType().getName().equals("Mountains")) {
                    int bonus = resourceChanges.getOrDefault(primaryOutputType, 0) / 4; // 25% bonus
                    resourceChanges.put(primaryOutputType, resourceChanges.get(primaryOutputType) + bonus);
                }
            }
        }

        return resourceChanges;
    }

    /**
     * Gets the input resource types required for this producer.
     *
     * @return List of input resource types
     */
    public List<ResourceType> getInputTypes() {
        return new ArrayList<>(inputTypes);
    }

    /**
     * Gets the input amounts required for each resource type.
     *
     * @return List of input amounts
     */
    public List<Integer> getInputAmounts() {
        return new ArrayList<>(inputAmounts);
    }

    /**
     * Gets the type of advanced producer.
     *
     * @return The advanced producer type
     */
    public AdvancedProducerType getProducerType() {
        return producerType;
    }

    /**
     * Enum for different types of advanced producers.
     */
    public enum AdvancedProducerType {
        FACTORY("Factory", "Converts materials into advanced components",
                ResourceType.MATERIALS, 15, 5, 8,
                List.of(ResourceType.MATERIALS, ResourceType.ENERGY),
                List.of(5, 2),
                Map.of(ResourceType.MATERIALS, 100, ResourceType.ENERGY, 50),
                Map.of(ResourceType.ENERGY, 5, ResourceType.MATERIALS, 1)),

        RESEARCH_LAB("Research Lab", "Generates research points for technology advancement",
                ResourceType.RESEARCH, 10, 4, 7,
                List.of(ResourceType.ENERGY),
                List.of(3),
                Map.of(ResourceType.MATERIALS, 80, ResourceType.ENERGY, 40),
                Map.of(ResourceType.ENERGY, 4)),

        ALIEN_ANALYZER("Alien Analyzer", "Analyzes and processes alien compounds",
                ResourceType.ALIEN_COMPOUNDS, 5, 3, 10,
                List.of(ResourceType.ENERGY, ResourceType.RESEARCH),
                List.of(5, 2),
                Map.of(ResourceType.MATERIALS, 120, ResourceType.ENERGY, 80, ResourceType.RARE_MINERALS, 10),
                Map.of(ResourceType.ENERGY, 8, ResourceType.MATERIALS, 2));

        private final String name;
        private final String description;
        private final ResourceType outputType;
        private final int baseOutput;
        private final int workersRequired;
        private final int constructionTime;
        private final List<ResourceType> inputTypes;
        private final List<Integer> inputAmounts;
        private final Map<ResourceType, Integer> constructionCost;
        private final Map<ResourceType, Integer> maintenanceCost;

        AdvancedProducerType(String name, String description,
                             ResourceType outputType, int baseOutput,
                             int workersRequired, int constructionTime,
                             List<ResourceType> inputTypes,
                             List<Integer> inputAmounts,
                             Map<ResourceType, Integer> constructionCost,
                             Map<ResourceType, Integer> maintenanceCost) {
            this.name = name;
            this.description = description;
            this.outputType = outputType;
            this.baseOutput = baseOutput;
            this.workersRequired = workersRequired;
            this.constructionTime = constructionTime;
            this.inputTypes = inputTypes;
            this.inputAmounts = inputAmounts;
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

        public List<ResourceType> getInputTypes() {
            return inputTypes;
        }

        public List<Integer> getInputAmounts() {
            return inputAmounts;
        }

        public Map<ResourceType, Integer> getConstructionCost() {
            return constructionCost;
        }

        public Map<ResourceType, Integer> getMaintenanceCost() {
            return maintenanceCost;
        }
    }
}