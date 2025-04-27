package com.colonygenesis.technology;

import com.colonygenesis.building.BuildingType;
import com.colonygenesis.core.Game;
import com.colonygenesis.resource.ResourceType;
import com.colonygenesis.ui.events.EventBus;
import com.colonygenesis.util.LoggerUtil;
import com.colonygenesis.util.Result;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Manages the research system and applies technology effects.
 */
public class TechManager implements Serializable {
    private static final Logger LOGGER = LoggerUtil.getLogger(TechManager.class);
    @Serial
    private static final long serialVersionUID = 1L;

    private final Game game;
    private final TechnologyTree techTree;
    private final Set<String> researchedTechs;
    private final Set<String> unlockedBuildings;

    // Effect trackers
    private final Map<ResourceType, Map<BuildingType, Double>> productionModifiers;
    private final Map<ResourceType, Map<BuildingType, Double>> consumptionModifiers;
    private final Map<BuildingType, Integer> workerReductions;
    private final Map<BuildingType, Double> constructionCostModifiers;
    private final Map<BuildingType, Double> constructionTimeModifiers;
    private double populationGrowthModifier = 1.0;
    private double baseEfficiencyWithoutWorkers = 0.0;  // Buildings don't work without workers by default

    private transient EventBus eventBus;

    public TechManager(Game game) {
        this.game = game;
        this.techTree = new TechnologyTree();
        this.researchedTechs = new HashSet<>();
        this.unlockedBuildings = new HashSet<>();
        this.productionModifiers = new HashMap<>();
        this.consumptionModifiers = new HashMap<>();
        this.workerReductions = new HashMap<>();
        this.constructionCostModifiers = new HashMap<>();
        this.constructionTimeModifiers = new HashMap<>();
        this.eventBus = EventBus.getInstance();
        initializeTechTree();
    }

    private void initializeTechTree() {
        ResearchTreeInitializer.populate(techTree);
    }

    public boolean canResearch(Technology tech) {
        // Check if already researched
        if (researchedTechs.contains(tech.getId())) {
            return false;
        }

        // Check prerequisites
        for (String prerequisite : tech.getTechPrerequisites()) {
            if (!researchedTechs.contains(prerequisite)) {
                return false;
            }
        }

        // Check resource costs
        for (Map.Entry<ResourceType, Integer> entry : tech.getResourceCosts().entrySet()) {
            if (game.getResourceManager().getResource(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }

        return true;
    }

    public Result<Technology> researchTechnology(String techId) {
        Technology tech = techTree.getTechnology(techId);
        if (tech == null) {
            return Result.failure("Technology not found: " + techId);
        }

        if (!canResearch(tech)) {
            return Result.failure("Cannot research " + tech.getName() + ": requirements not met");
        }

        // Deduct resources
        for (Map.Entry<ResourceType, Integer> entry : tech.getResourceCosts().entrySet()) {
            Result<Integer> result = game.getResourceManager().removeResource(entry.getKey(), entry.getValue());
            if (result.isFailure()) {
                return Result.failure("Failed to deduct resources: " + result.getErrorMessage());
            }
        }

        // Mark as researched
        researchedTechs.add(tech.getId());

        // Apply effects
        for (TechEffect effect : tech.getEffects()) {
            try {
                effect.apply(game);
                LOGGER.info("Applied effect: " + effect.getDescription());
            } catch (Exception e) {
                LOGGER.warning("Failed to apply effect: " + effect.getDescription() + " - " + e.getMessage());
            }
        }

        // Fire event
        eventBus.publish(new TechEvents.TechnologyResearchedEvent(tech));

        LOGGER.info("Technology researched: " + tech.getName());
        return Result.success(tech);
    }

    // Methods for managing effects

    public void addUnlockedBuilding(String buildingId) {
        unlockedBuildings.add(buildingId);
    }

    public boolean isBuildingUnlocked(String buildingId) {
        return unlockedBuildings.contains(buildingId);
    }

    public void addProductionModifier(ResourceType resourceType, double modifier, BuildingType buildingType) {
        productionModifiers.computeIfAbsent(resourceType, k -> new HashMap<>())
                .put(buildingType, modifier);
    }

    public double getProductionModifier(ResourceType resourceType, BuildingType buildingType) {
        Map<BuildingType, Double> modifiers = productionModifiers.get(resourceType);
        if (modifiers != null) {
            Double specific = modifiers.get(buildingType);
            Double global = modifiers.get(null);  // null represents all buildings

            double total = 1.0;
            if (specific != null) total *= specific;
            if (global != null) total *= global;
            return total;
        }
        return 1.0;
    }

    public void addConsumptionModifier(ResourceType resourceType, double modifier, BuildingType buildingType) {
        consumptionModifiers.computeIfAbsent(resourceType, k -> new HashMap<>())
                .put(buildingType, modifier);
    }

    public double getConsumptionModifier(ResourceType resourceType, BuildingType buildingType) {
        Map<BuildingType, Double> modifiers = consumptionModifiers.get(resourceType);
        if (modifiers != null) {
            Double specific = modifiers.get(buildingType);
            Double global = modifiers.get(null);  // null represents all buildings

            double total = 1.0;
            if (specific != null) total *= specific;
            if (global != null) total *= global;
            return total;
        }
        return 1.0;
    }

    public void addWorkerReduction(int reduction, BuildingType buildingType) {
        workerReductions.merge(buildingType, reduction, Integer::sum);
    }

    public int getWorkerReduction(BuildingType buildingType) {
        int reduction = workerReductions.getOrDefault(buildingType, 0);
        Integer globalReduction = workerReductions.get(null);  // null represents all buildings
        if (globalReduction != null) {
            reduction += globalReduction;
        }
        return reduction;
    }

    public void addConstructionCostModifier(double modifier, BuildingType buildingType) {
        constructionCostModifiers.put(buildingType, modifier);
    }

    public double getConstructionCostModifier(BuildingType buildingType) {
        Double specific = constructionCostModifiers.get(buildingType);
        Double global = constructionCostModifiers.get(null);  // null represents all buildings

        double total = 1.0;
        if (specific != null) total *= specific;
        if (global != null) total *= global;
        return total;
    }

    public void addConstructionTimeModifier(double modifier, BuildingType buildingType) {
        constructionTimeModifiers.put(buildingType, modifier);
    }

    public double getConstructionTimeModifier(BuildingType buildingType) {
        Double specific = constructionTimeModifiers.get(buildingType);
        Double global = constructionTimeModifiers.get(null);  // null represents all buildings

        double total = 1.0;
        if (specific != null) total *= specific;
        if (global != null) total *= global;
        return total;
    }

    public void addPopulationGrowthModifier(double modifier) {
        populationGrowthModifier *= modifier;
    }

    public double getPopulationGrowthModifier() {
        return populationGrowthModifier;
    }

    public void setBaseEfficiency(double efficiency) {
        this.baseEfficiencyWithoutWorkers = efficiency;
    }

    public double getBaseEfficiency() {
        return baseEfficiencyWithoutWorkers;
    }

    public List<Technology> getAvailableTechnologies() {
        return techTree.getAllTechnologies().stream()
                .filter(this::canResearch)
                .toList();
    }

    public boolean isTechResearched(String techId) {
        return researchedTechs.contains(techId);
    }

    public TechnologyTree getTechTree() {
        return techTree;
    }

    public Game getGame() {
        return game;
    }

    public Set<String> getResearchedTechs() {
        return new HashSet<>(researchedTechs);
    }

    @Serial
    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        this.eventBus = EventBus.getInstance();
    }
}