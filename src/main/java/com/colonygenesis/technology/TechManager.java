package com.colonygenesis.technology;

import com.colonygenesis.core.Game;
import com.colonygenesis.resource.ResourceType;
import com.colonygenesis.ui.events.EventBus;
import com.colonygenesis.util.LoggerUtil;
import com.colonygenesis.util.Result;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Manages the research system.
 */
public class TechManager implements Serializable {
    private static final Logger LOGGER = LoggerUtil.getLogger(TechManager.class);
    @Serial
    private static final long serialVersionUID = 1L;

    private final Game game;
    private final TechnologyTree techTree;
    private final Set<String> researchedTechs;
    private transient EventBus eventBus;

    public TechManager(Game game) {
        this.game = game;
        this.techTree = new TechnologyTree();
        this.researchedTechs = new HashSet<>();
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

        // Fire event
        eventBus.publish(new TechEvents.TechnologyResearchedEvent(tech));

        LOGGER.info("Technology researched: " + tech.getName());
        return Result.success(tech);
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

    /**
     * Gets the game instance.
     *
     * @return The game instance
     */
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