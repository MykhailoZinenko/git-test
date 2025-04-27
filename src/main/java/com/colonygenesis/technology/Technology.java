package com.colonygenesis.technology;

import com.colonygenesis.resource.ResourceType;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * Represents a technology in the research tree.
 */
public class Technology implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String name;
    private final String description;
    private final TechBranch branch;
    private final int tier;
    private final Map<ResourceType, Integer> resourceCosts;
    private final Set<String> techPrerequisites;
    private final List<TechEffect> effects;

    // Visual positioning for the tree
    private final double xPosition;
    private final double yPosition;

    public Technology(String id, String name, String description, TechBranch branch, int tier,
                      double xPosition, double yPosition) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.branch = branch;
        this.tier = tier;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.resourceCosts = new EnumMap<>(ResourceType.class);
        this.techPrerequisites = new HashSet<>();
        this.effects = new ArrayList<>();
    }

    public void addResourceCost(ResourceType type, int amount) {
        resourceCosts.put(type, amount);
    }

    public void addPrerequisite(String techId) {
        techPrerequisites.add(techId);
    }

    public void addEffect(TechEffect effect) {
        effects.add(effect);
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public TechBranch getBranch() { return branch; }
    public int getTier() { return tier; }
    public Map<ResourceType, Integer> getResourceCosts() { return new EnumMap<>(resourceCosts); }
    public Set<String> getTechPrerequisites() { return new HashSet<>(techPrerequisites); }
    public List<TechEffect> getEffects() { return new ArrayList<>(effects); }
    public double getXPosition() { return xPosition; }
    public double getYPosition() { return yPosition; }
}

