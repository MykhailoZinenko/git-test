package com.colonygenesis.building;

import com.colonygenesis.core.Game;
import com.colonygenesis.map.Tile;
import com.colonygenesis.resource.ResourceType;

import java.io.Serial;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class for buildings that produce resources.
 * Second layer in the building hierarchy.
 */
public abstract class ProductionBuilding extends AbstractBuilding {
    @Serial
    private static final long serialVersionUID = 1L;

    protected ResourceType primaryOutputType;
    protected int baseOutputAmount;
    protected Map<String, Float> productionModifiers;

    /**
     * Constructs a new production building.
     *
     * @param name The name of the building
     * @param description A brief description of the building
     * @param location The tile where the building is located
     * @param constructionTime The number of turns to construct the building
     * @param workersRequired The number of workers required to operate the building
     * @param primaryOutputType The main resource this building produces
     * @param baseOutputAmount The base amount of resource produced per turn
     */
    public ProductionBuilding(String name, String description, Tile location,
                              int constructionTime, int workersRequired,
                              ResourceType primaryOutputType, int baseOutputAmount, Game game) {
        super(name, description, location, constructionTime, workersRequired, BuildingType.PRODUCTION, game);

        this.primaryOutputType = primaryOutputType;
        this.baseOutputAmount = baseOutputAmount;
        this.productionModifiers = new HashMap<>();

        this.productionModifiers.put("base", 1.0f);

        if (location != null) {
            float terrainModifier = (float) location.getResourceYield(primaryOutputType);
            this.productionModifiers.put("terrain", terrainModifier);
        }
    }

    /**
     * Calculates the total production output considering all modifiers.
     *
     * @return The total amount of resources produced
     */
    @Override
    protected int calculateProduction() {
        if (!isActive()) {
            return 0;
        }

        float totalModifier = 1.0f;

        // Apply building-specific modifiers
        for (float modifier : productionModifiers.values()) {
            totalModifier *= modifier;
        }

        // Apply tech modifiers
        if (game != null && game.getTechManager() != null) {
            double techModifier = game.getTechManager().getProductionModifier(primaryOutputType, buildingType);
            totalModifier *= techModifier;
        }

        System.out.println(baseOutputAmount + " " + totalModifier);

        float workerEfficiency = calculateEfficiency() / 100.0f;

        return Math.round(baseOutputAmount * totalModifier * workerEfficiency);
    }

    /**
     * Adds a production modifier with the specified name and value.
     *
     * @param name The name of the modifier
     * @param value The value of the modifier (1.0 = 100%)
     */
    public void addProductionModifier(String name, float value) {
        productionModifiers.put(name, value);
        LOGGER.fine("Added production modifier to " + getName() + ": " + name + " = " + value);
    }

    /**
     * Removes a production modifier with the specified name.
     *
     * @param name The name of the modifier to remove
     */
    public void removeProductionModifier(String name) {
        if (productionModifiers.remove(name) != null) {
            LOGGER.fine("Removed production modifier from " + getName() + ": " + name);
        }
    }

    /**
     * Gets the current production efficiency as a percentage.
     *
     * @return The efficiency percentage (0-100)
     */
    public int getEfficiency() {
        float totalModifier = 1.0f;
        for (float modifier : productionModifiers.values()) {
            totalModifier *= modifier;
        }

        float workerEfficiency = (float) workersAssigned / workersRequired;
        if (workerEfficiency > 1.0f) workerEfficiency = 1.0f;

        return Math.round(totalModifier * workerEfficiency * 100);
    }

    @Override
    public Map<ResourceType, Integer> operate() {
        Map<ResourceType, Integer> output = new EnumMap<>(ResourceType.class);

        if (isActive()) {
            int productionAmount = calculateProduction();
            output.put(primaryOutputType, productionAmount);
            LOGGER.fine(getName() + " produced " + productionAmount + " " + primaryOutputType.getName());
        }

        return output;
    }

    public ResourceType getPrimaryOutputType() {
        return primaryOutputType;
    }

    public int getBaseOutputAmount() {
        return baseOutputAmount;
    }

    public Map<String, Float> getProductionModifiers() {
        return new HashMap<>(productionModifiers);
    }
}