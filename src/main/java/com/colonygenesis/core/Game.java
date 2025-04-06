package com.colonygenesis.core;

import com.colonygenesis.building.BuildingManager;
import com.colonygenesis.map.Planet;
import com.colonygenesis.map.PlanetType;
import com.colonygenesis.resource.ResourceManager;
import com.colonygenesis.ui.events.EventBus;
import com.colonygenesis.ui.events.TurnEvents;
import com.colonygenesis.util.DialogUtil;
import com.colonygenesis.util.LoggerUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Core game class that manages the game state and provides
 * game initialization, saving, and loading functionality.
 */
public class Game implements Serializable {
    private static final Logger LOGGER = LoggerUtil.getLogger(Game.class);

    @Serial
    private static final long serialVersionUID = 1L;

    private boolean initialized = false;
    private boolean running = false;
    private boolean paused = false;

    private String colonyName;
    private PlanetType planetType;
    private int mapSize;
    private int currentTurn = 1;
    private LocalDateTime saveDate;

    private ResourceManager resourceManager;
    private TurnManager turnManager;
    private BuildingManager buildingManager;
    private Planet planet;

    /**
     * Constructs a new game instance.
     */
    public Game() {
        LOGGER.fine("Game instance created");
    }

    /**
     * Initializes the game with the specified parameters.
     *
     * @param colonyName The name of the player's colony
     * @param planetType The type of planet to generate
     * @param mapSize The size of the game map
     */
    public void initialize(String colonyName, PlanetType planetType, int mapSize) {
        LOGGER.info("Initializing game with colony: " + colonyName +
                ", planet type: " + planetType + ", map size: " + mapSize);

        this.initialized = false;
        this.running = false;
        this.paused = false;

        this.colonyName = colonyName;
        this.planetType = planetType;
        this.mapSize = mapSize;
        this.currentTurn = 1;
        this.saveDate = null;

        this.resourceManager = new ResourceManager();
        this.turnManager = new TurnManager(this);
        this.buildingManager = new BuildingManager(this);
        this.planet = new Planet(this, colonyName + " Prime", planetType, mapSize);

        this.initialized = true;

        LOGGER.info("Game initialization complete");
    }

    /**
     * Starts the game.
     *
     * @throws IllegalStateException if the game is not initialized
     */
    public void start() {
        if (!initialized) {
            LOGGER.severe("Attempted to start game before initialization");
            throw new IllegalStateException("Game must be initialized before starting");
        }

        EventBus.getInstance().reset();

        LOGGER.info("Starting game: " + colonyName);
        this.running = true;
        this.paused = false;

        if (resourceManager != null) {
            resourceManager.publishCurrentState();
        }

        EventBus.getInstance().publish(new TurnEvents.TurnAdvancedEvent(
                currentTurn,
                currentTurn - 1
        ));

        EventBus.getInstance().publish(new TurnEvents.PhaseChangedEvent(
                turnManager.getCurrentPhase(),
                turnManager.getCurrentPhase(),
                currentTurn
        ));
    }

    /**
     * Pauses the game.
     */
    public void pause() {
        LOGGER.info("Game paused: " + colonyName);
        this.paused = true;
    }

    /**
     * Resumes the game from a paused state.
     */
    public void resume() {
        LOGGER.info("Game resumed: " + colonyName);
        this.paused = false;
    }

    /**
     * Stops the game.
     */
    public void stop() {
        LOGGER.info("Game stopped: " + colonyName);
        this.running = false;
    }

    /**
     * Advances the game to the next turn.
     */
    public void advanceTurn() {
        LOGGER.info("Advancing from turn " + currentTurn + " to " + (currentTurn + 1));

        resourceManager.processTurn();

        buildingManager.processTurn();

        currentTurn++;

        LOGGER.info("Advanced to turn " + currentTurn);
    }

    /**
     * Validates the game state before saving.
     * @return true if the game state is valid, false otherwise
     */
    private boolean validateGameState() {
        boolean valid = true;

        if (resourceManager == null) {
            LOGGER.severe("Invalid game state: ResourceManager is null");
            valid = false;
        }

        if (turnManager == null) {
            LOGGER.severe("Invalid game state: TurnManager is null");
            valid = false;
        }

        if (buildingManager == null) {
            LOGGER.severe("Invalid game state: BuildingManager is null");
            valid = false;
        }

        if (planet == null) {
            LOGGER.severe("Invalid game state: Planet is null");
            valid = false;
        } else if (planet.getGrid() == null) {
            LOGGER.severe("Invalid game state: Planet grid is null");
            valid = false;
        }

        return !valid;
    }

    /**
     * Saves the current game state to a file.
     *
     * @return The filename of the saved game, or null if the save failed
     */
    public String saveGame() {
        try {
            if (validateGameState()) {
                LOGGER.severe("Cannot save game: invalid game state");
                return null;
            }

            Path savesDir = Paths.get("saves");
            if (!Files.exists(savesDir)) {
                Files.createDirectories(savesDir);
            }

            this.saveDate = LocalDateTime.now();

            String timestamp = saveDate.format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            String sanitizedColonyName = colonyName.replaceAll("[^a-zA-Z0-9]", "_");

            String filename = "saves/" + timestamp + "_" + sanitizedColonyName + "_Turn" + currentTurn + ".save";

            LOGGER.info("Saving game to: " + filename);

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
                oos.writeObject(this);
            }

            LOGGER.info("Game saved successfully");
            return filename;

        } catch (IOException e) {
            LOGGER.severe("Error saving game: " + e.getMessage());
            return null;
        }
    }

    public static Game loadGame(String filename) {
        try {
            LOGGER.info("Loading game from: " + filename);

            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
                Game loadedGame = (Game) ois.readObject();

                loadedGame.reconnectAfterLoading();

                if (loadedGame.validateGameState()) {
                    LOGGER.severe("Loaded game has invalid state after reconnection");
                    return null;
                }

                LOGGER.info("Game loaded successfully: " + loadedGame.getColonyName() +
                        ", Turn: " + loadedGame.getCurrentTurn());
                return loadedGame;
            }
        } catch (Exception e) {
            LOGGER.severe("Error loading game: " + e.getMessage());

            Game recoveryGame = new Game();
            String baseFilename = Paths.get(filename).getFileName().toString();
            if (baseFilename.contains("_")) {
                String[] parts = baseFilename.split("_");
                if (parts.length >= 2) {
                    String colonyName = parts[1].replace("_", " ");
                    recoveryGame.initialize(colonyName, PlanetType.TEMPERATE, 30);
                    recoveryGame.start();
                    LOGGER.info("Created recovery game with name: " + colonyName);
                    DialogUtil.showMessageDialog("Loading Error",
                            "The save file could not be loaded. A new game has been created.");
                    return recoveryGame;
                }
            }

            return null;
        }
    }

    /**
     * Reconnects all components after loading from a saved game.
     * This ensures all transient fields are properly reinitialized.
     */
    private void reconnectAfterLoading() {
        LOGGER.info("Reconnecting game components after loading");

        if (resourceManager != null) {
            resourceManager.publishCurrentState();
        } else {
            LOGGER.severe("ResourceManager is null after loading");
            resourceManager = new ResourceManager();
        }

        if (turnManager != null) {
            turnManager.setGame(this);
        } else {
            LOGGER.severe("TurnManager is null after loading");
            turnManager = new TurnManager(this);
        }

        if (buildingManager != null) {
            buildingManager.setGame(this);
            buildingManager.updateResourceManagerReferences();
        } else {
            LOGGER.severe("BuildingManager is null after loading");
            buildingManager = new BuildingManager(this);
        }

        EventBus eventBus = EventBus.getInstance();

        resourceManager.publishCurrentState();

        eventBus.publish(new TurnEvents.TurnAdvancedEvent(currentTurn, currentTurn - 1));
        eventBus.publish(new TurnEvents.PhaseChangedEvent(
                turnManager.getCurrentPhase(), turnManager.getCurrentPhase(), currentTurn
        ));

        LOGGER.info("Game reconnection complete");
    }

    /**
     * Custom deserialization to ensure all transient fields are properly initialized.
     *
     * @param in The object input stream
     * @throws IOException If an I/O error occurs
     * @throws ClassNotFoundException If the class cannot be found
     */
    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        if (turnManager != null) {
            turnManager.setGame(this);
        }

        if (buildingManager != null) {
            buildingManager.setGame(this);
            buildingManager.updateResourceManagerReferences();
        }

        LOGGER.info("Game deserialized: " + colonyName + ", Turn: " + currentTurn);
    }

    /**
     * Gets a list of all saved games.
     *
     * @return A list of saved game information
     */
    public static List<SaveGameInfo> getSavedGames() {
        List<SaveGameInfo> savedGames = new ArrayList<>();
        Path savesDir = Paths.get("saves");

        if (!Files.exists(savesDir)) {
            try {
                Files.createDirectories(savesDir);
            } catch (IOException e) {
                LOGGER.severe("Error creating saves directory: " + e.getMessage());
                return savedGames;
            }
        }

        try {
            Files.list(savesDir)
                    .filter(path -> path.toString().endsWith(".save"))
                    .forEach(path -> {
                        try {
                            LOGGER.info("Loading save info from file: " + path);
                            Game game = loadGameInfo(path.toString());
                            if (game != null) {
                                SaveGameInfo saveInfo = new SaveGameInfo(
                                        path.toString(),
                                        game.getColonyName(),
                                        game.getPlanetType(),
                                        game.getCurrentTurn(),
                                        game.getSaveDate(),
                                        game.getMapSize()
                                );
                                savedGames.add(saveInfo);
                                LOGGER.info("Added save info: " + saveInfo);
                            }
                        } catch (Exception e) {
                            LOGGER.warning("Error loading save info from: " + path + " - " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
            LOGGER.severe("Error listing save files: " + e.getMessage());
        }

        return savedGames;
    }

    /**
     * Loads only the basic information from a save file without fully loading the game.
     * This is more efficient for displaying save info.
     *
     * @param filename The filename of the saved game
     * @return A Game object with basic information, or null if loading failed
     */
    private static Game loadGameInfo(String filename) {
        try {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
                return (Game) ois.readObject();
            }
        } catch (Exception e) {
            LOGGER.warning("Error loading save info from: " + filename + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * Performs cleanup of any static resources before starting a new game.
     * This helps prevent state leakage between game instances.
     */
    public static void cleanup() {
        LOGGER.info("Performing static game cleanup");

        EventBus.getInstance().reset();

        System.gc();
    }

    /**
     * Gets the resource manager.
     *
     * @return The resource manager
     */
    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    /**
     * Gets the turn manager.
     *
     * @return The turn manager
     */
    public TurnManager getTurnManager() {
        return turnManager;
    }

    /**
     * Gets the building manager.
     *
     * @return The building manager
     */
    public BuildingManager getBuildingManager() {
        return buildingManager;
    }

    /**
     * Gets the planet.
     *
     * @return The planet
     */
    public Planet getPlanet() {
        return planet;
    }

    /**
     * Checks if the game is initialized.
     *
     * @return true if the game is initialized, false otherwise
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Checks if the game is running.
     *
     * @return true if the game is running, false otherwise
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Checks if the game is paused.
     *
     * @return true if the game is paused, false otherwise
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Gets the colony name.
     *
     * @return The colony name
     */
    public String getColonyName() {
        return colonyName;
    }

    /**
     * Gets the planet type.
     *
     * @return The planet type
     */
    public PlanetType getPlanetType() {
        return planetType;
    }

    /**
     * Gets the map size.
     *
     * @return The map size
     */
    public int getMapSize() {
        return mapSize;
    }

    /**
     * Gets the current turn number.
     *
     * @return The current turn
     */
    public int getCurrentTurn() {
        return currentTurn;
    }

    /**
     * Sets the current turn number.
     *
     * @param currentTurn The new turn number
     */
    public void setCurrentTurn(int currentTurn) {
        LOGGER.fine("Changing turn from " + this.currentTurn + " to " + currentTurn);
        this.currentTurn = currentTurn;
    }

    /**
     * Gets the date when the game was saved.
     *
     * @return The save date, or null if the game has not been saved
     */
    public LocalDateTime getSaveDate() {
        return saveDate;
    }

    /**
     * Record class for save game information.
     */
    public record SaveGameInfo(String filename, String colonyName, PlanetType planetType, int turn,
                               LocalDateTime saveDate, int mapSize) {

        @Override
        public String toString() {
            return colonyName + " (" + planetType + ") - Turn " + turn + " - Map: " + mapSize + "x" + mapSize + " - " +
                    saveDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }
}