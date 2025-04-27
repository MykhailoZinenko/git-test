package com.colonygenesis.technology;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * Manages the technology tree structure.
 */
public class TechnologyTree implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Map<String, Technology> technologies;
    private final Map<String, List<String>> dependencies;

    public TechnologyTree() {
        this.technologies = new HashMap<>();
        this.dependencies = new HashMap<>();
    }

    public void addTechnology(Technology tech) {
        technologies.put(tech.getId(), tech);

        // Build dependency map
        for (String prerequisite : tech.getTechPrerequisites()) {
            dependencies.computeIfAbsent(prerequisite, k -> new ArrayList<>()).add(tech.getId());
        }
    }

    public Technology getTechnology(String id) {
        return technologies.get(id);
    }

    public Collection<Technology> getAllTechnologies() {
        return technologies.values();
    }

    public List<Technology> getTechnologiesByBranch(TechBranch branch) {
        return technologies.values().stream()
                .filter(tech -> tech.getBranch() == branch)
                .toList();
    }

    public List<Technology> getTechnologiesByTier(int tier) {
        return technologies.values().stream()
                .filter(tech -> tech.getTier() == tier)
                .toList();
    }

    public List<String> getDependentTechnologies(String techId) {
        return dependencies.getOrDefault(techId, Collections.emptyList());
    }
}