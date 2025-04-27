package com.colonygenesis.victory;

import com.colonygenesis.core.Game;
import com.colonygenesis.technology.TechManager;
import com.colonygenesis.building.BuildingManager;
import com.colonygenesis.building.BuildingType;
import com.colonygenesis.building.AbstractBuilding;
import com.colonygenesis.building.UnlockedProducers;
import com.colonygenesis.map.HexGrid;
import com.colonygenesis.resource.ResourceManager;
import com.colonygenesis.resource.ResourceType;
import com.colonygenesis.ui.events.EventBus;
import com.colonygenesis.util.LoggerUtil;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Manages victory conditions and tracks progress towards victory.
 */
public class VictoryManager implements Serializable {
    private static final Logger LOGGER = LoggerUtil.getLogger(VictoryManager.class);
    @Serial
    private static final long serialVersionUID = 1L;

    private final Game game;
    private final List<VictoryCondition> victoryConditions;
    private final Map<VictoryType, Float> progress;
    private final Map<VictoryType, Boolean> unlockedConditions;
    private final Map<VictoryType, Boolean> acknowledgedVictories;

    // Scientific Victory Requirements
    private static final String[] SCIENTIFIC_KEY_TECHS = {"tech_singularity", "artificial_intelligence", "alien_tech_mastery"};

    // Industrial Victory Requirements
    private static final String[] INDUSTRIAL_KEY_TECHS = {"megastructure", "orbital_manufacturing", "resource_refinement"};
    private static final String MEGASTRUCTURE_BUILDING_ID = "alien_megastructure";

    // Harmony Victory Requirements
    private static final String[] HARMONY_KEY_TECHS = {"planetary_harmony", "integrated_ecosystem", "symbiotic_systems"};
    private static final int HARMONY_SUSTAINABLE_TURNS = 20;
    private int sustainableTurns = 0;

    // Expansionist Victory Requirements
    private static final int EXPANSION_POPULATION_TARGET = 1000;
    private static final int EXPANSION_TILE_PERCENTAGE = 75;
    private static final int EXPANSION_BUILDING_COUNT = 50;

    public VictoryManager(Game game) {
        this.game = game;
        this.victoryConditions = new ArrayList<>();
        this.progress = new HashMap<>();
        this.unlockedConditions = new HashMap<>();
        this.acknowledgedVictories = new HashMap<>();

        // Initialize victory types
        for (VictoryType type : VictoryType.values()) {
            progress.put(type, 0.0f);
            unlockedConditions.put(type, false);
            acknowledgedVictories.put(type, false);
        }

        // Create victory conditions
        initializeVictoryConditions();
    }

    private void initializeVictoryConditions() {
        // Scientific Victory
        victoryConditions.add(new VictoryCondition() {
            @Override
            public boolean checkProgress(Game game) {
                TechManager techManager = game.getTechManager();
                int researchedCount = 0;

                for (String techId : SCIENTIFIC_KEY_TECHS) {
                    if (techManager.isTechResearched(techId)) {
                        researchedCount++;
                    }
                }

                float progressValue = (float) researchedCount / SCIENTIFIC_KEY_TECHS.length;
                progress.put(VictoryType.SCIENTIFIC, progressValue);

                return progressValue >= 1.0f;
            }

            @Override
            public boolean isAchieved(Game game) {
                return checkProgress(game) && isUnlocked(VictoryType.SCIENTIFIC);
            }

            @Override
            public String getDescription() {
                return "Research all key scientific technologies including Technological Singularity";
            }

            @Override
            public VictoryType getType() {
                return VictoryType.SCIENTIFIC;
            }
        });

        // Industrial Victory
        victoryConditions.add(new VictoryCondition() {
            @Override
            public boolean checkProgress(Game game) {
                TechManager techManager = game.getTechManager();
                BuildingManager buildingManager = game.getBuildingManager();

                // Check tech progress
                int researchedCount = 0;
                for (String techId : INDUSTRIAL_KEY_TECHS) {
                    if (techManager.isTechResearched(techId)) {
                        researchedCount++;
                    }
                }

                // Check for megastructure by building ID
                boolean hasMegastructure = false;
                for (AbstractBuilding building : buildingManager.getAllBuildings()) {
                    if (building instanceof UnlockedProducers) {
                        UnlockedProducers unlockedBuilding = (UnlockedProducers) building;
                        if (unlockedBuilding.getProducerType().getUnlockId().equals(MEGASTRUCTURE_BUILDING_ID)) {
                            hasMegastructure = true;
                            break;
                        }
                    }
                }

                float progressValue = ((float) researchedCount / INDUSTRIAL_KEY_TECHS.length) * 0.7f
                        + (hasMegastructure ? 0.3f : 0.0f);
                progress.put(VictoryType.INDUSTRIAL, progressValue);

                return progressValue >= 1.0f;
            }

            @Override
            public boolean isAchieved(Game game) {
                return checkProgress(game) && isUnlocked(VictoryType.INDUSTRIAL);
            }

            @Override
            public String getDescription() {
                return "Master industrial technology and build the Alien Megastructure";
            }

            @Override
            public VictoryType getType() {
                return VictoryType.INDUSTRIAL;
            }
        });

        // Harmony Victory
        victoryConditions.add(new VictoryCondition() {
            @Override
            public boolean checkProgress(Game game) {
                TechManager techManager = game.getTechManager();
                ResourceManager resourceManager = game.getResourceManager();

                // Check tech progress
                int researchedCount = 0;
                for (String techId : HARMONY_KEY_TECHS) {
                    if (techManager.isTechResearched(techId)) {
                        researchedCount++;
                    }
                }

                // Check sustainability (production >= consumption for all resources)
                boolean isSustainable = true;
                for (ResourceType type : ResourceType.values()) {
                    if (type != ResourceType.ALIEN_COMPOUNDS && type != ResourceType.RARE_MINERALS) {
                        if (resourceManager.getNetProduction(type) < 0) {
                            isSustainable = false;
                            sustainableTurns = 0;
                            break;
                        }
                    }
                }

                if (isSustainable) {
                    sustainableTurns++;
                }

                float progressValue = ((float) researchedCount / HARMONY_KEY_TECHS.length) * 0.7f
                        + ((float) sustainableTurns / HARMONY_SUSTAINABLE_TURNS) * 0.3f;
                progress.put(VictoryType.HARMONY, progressValue);

                return progressValue >= 1.0f;
            }

            @Override
            public boolean isAchieved(Game game) {
                return checkProgress(game) && isUnlocked(VictoryType.HARMONY);
            }

            @Override
            public String getDescription() {
                return "Achieve planetary harmony by researching key technologies and maintaining sustainability for " +
                        HARMONY_SUSTAINABLE_TURNS + " turns";
            }

            @Override
            public VictoryType getType() {
                return VictoryType.HARMONY;
            }
        });

        // Expansionist Victory
        victoryConditions.add(new VictoryCondition() {
            @Override
            public boolean checkProgress(Game game) {
                ResourceManager resourceManager = game.getResourceManager();
                HexGrid grid = game.getPlanet().getGrid();
                BuildingManager buildingManager = game.getBuildingManager();

                int population = resourceManager.getResource(ResourceType.POPULATION);

                // Count colonized tiles
                int totalTiles = grid.getWidth() * grid.getHeight();
                int colonizedTiles = 0;

                for (int x = 0; x < grid.getWidth(); x++) {
                    for (int y = 0; y < grid.getHeight(); y++) {
                        if (grid.getTileAt(x, y).isColonized()) {
                            colonizedTiles++;
                        }
                    }
                }

                int buildingCount = buildingManager.getBuildingCount();

                float populationProgress = Math.min((float) population / EXPANSION_POPULATION_TARGET, 1.0f);
                float tileProgress = Math.min((float) colonizedTiles / (totalTiles * EXPANSION_TILE_PERCENTAGE / 100), 1.0f);
                float buildingProgress = Math.min((float) buildingCount / EXPANSION_BUILDING_COUNT, 1.0f);

                float progressValue = (populationProgress + tileProgress + buildingProgress) / 3.0f;
                progress.put(VictoryType.EXPANSIONIST, progressValue);

                return progressValue >= 1.0f;
            }

            @Override
            public boolean isAchieved(Game game) {
                return checkProgress(game);
            }

            @Override
            public String getDescription() {
                return "Expand your colony to " + EXPANSION_POPULATION_TARGET + " population, colonize " +
                        EXPANSION_TILE_PERCENTAGE + "% of tiles, and build " + EXPANSION_BUILDING_COUNT + " buildings";
            }

            @Override
            public VictoryType getType() {
                return VictoryType.EXPANSIONIST;
            }
        });
    }

    /**
     * Checks if the game is over due to resource depletion.
     */
    public boolean checkGameOver() {
        System.out.println("checking game over");
        ResourceManager resourceManager = game.getResourceManager();

        // Check critical resources
        if (resourceManager.getResource(ResourceType.POPULATION) <= 0) {
            LOGGER.info("Game Over: Population reached zero");
            EventBus.getInstance().publish(new VictoryEvents.GameOverEvent("Population Extinction",
                    "Your colony's population has reached zero. The colony has failed."));
            return true;
        }

        if (resourceManager.getResource(ResourceType.FOOD) <= 0 &&
                resourceManager.getNetProduction(ResourceType.FOOD) < 0) {
            LOGGER.info("Game Over: Food shortage");
            EventBus.getInstance().publish(new VictoryEvents.GameOverEvent("Starvation",
                    "Your colony has run out of food. The colony has failed."));
            return true;
        }

        if (resourceManager.getResource(ResourceType.WATER) <= 0 &&
                resourceManager.getNetProduction(ResourceType.WATER) < 0) {
            LOGGER.info("Game Over: Water shortage");
            EventBus.getInstance().publish(new VictoryEvents.GameOverEvent("Dehydration",
                    "Your colony has run out of water. The colony has failed."));
            return true;
        }

        if (resourceManager.getResource(ResourceType.ENERGY) <= 0 &&
                resourceManager.getNetProduction(ResourceType.ENERGY) < 0) {
            LOGGER.info("Game Over: Energy crisis");
            EventBus.getInstance().publish(new VictoryEvents.GameOverEvent("Energy Crisis",
                    "Your colony has run out of energy. The colony has failed."));
            return true;
        }

        return false;
    }

    /**
     * Checks all victory conditions and returns true if any are achieved.
     */
    public boolean checkVictoryConditions() {
        for (VictoryCondition condition : victoryConditions) {
            if (condition.isAchieved(game) && !acknowledgedVictories.get(condition.getType())) {
                LOGGER.info("Victory achieved: " + condition.getType());
                EventBus.getInstance().publish(new VictoryEvents.VictoryAchievedEvent(condition.getType()));
                return true;
            }
        }
        return false;
    }

    /**
     * Marks a victory as acknowledged by the player.
     */
    public void acknowledgeVictory(VictoryType type) {
        acknowledgedVictories.put(type, true);
        LOGGER.info("Victory acknowledged: " + type);
    }

    /**
     * Updates progress for all victory conditions.
     */
    public void updateProgress() {
        for (VictoryCondition condition : victoryConditions) {
            condition.checkProgress(game);
        }
    }

    /**
     * Unlocks a victory condition (usually through technology).
     */
    public void unlockVictoryCondition(VictoryType type) {
        unlockedConditions.put(type, true);
        LOGGER.info("Unlocked victory condition: " + type);
    }

    /**
     * Checks if a victory condition is unlocked.
     */
    public boolean isUnlocked(VictoryType type) {
        return unlockedConditions.getOrDefault(type, false);
    }

    /**
     * Gets the progress percentage for a victory type.
     */
    public float getProgress(VictoryType type) {
        return progress.getOrDefault(type, 0.0f);
    }

    /**
     * Gets all victory conditions.
     */
    public List<VictoryCondition> getVictoryConditions() {
        return new ArrayList<>(victoryConditions);
    }
}