package com.colonygenesis.core;

import com.colonygenesis.map.PlanetType;
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

        this.colonyName = colonyName;
        this.planetType = planetType;
        this.mapSize = mapSize;
        this.currentTurn = 1;
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

        LOGGER.info("Starting game: " + colonyName);
        this.running = true;
        this.paused = false;
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
     * Saves the current game state to a file.
     *
     * @return The filename of the saved game, or null if the save failed
     */
    public String saveGame() {
        try {
            Path savesDir = Paths.get("saves");
            if (!Files.exists(savesDir)) {
                Files.createDirectories(savesDir);
            }

            this.saveDate = LocalDateTime.now();

            String timestamp = saveDate.format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            String filename = "saves/" + timestamp + "_" + colonyName.replaceAll("\\s+", "_") + ".save";

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

    /**
     * Loads a game from a saved file.
     *
     * @param filename The filename of the saved game
     * @return The loaded game, or null if loading failed
     */
    public static Game loadGame(String filename) {
        try {
            LOGGER.info("Loading game from: " + filename);

            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
                Game loadedGame = (Game) ois.readObject();
                LOGGER.info("Game loaded successfully: " + loadedGame.getColonyName());
                return loadedGame;
            }
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.severe("Error loading game: " + e.getMessage());
            return null;
        }
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
                            Game game = loadGame(path.toString());
                            if (game != null) {
                                SaveGameInfo saveInfo = new SaveGameInfo(
                                        path.toString(),
                                        game.getColonyName(),
                                        game.getPlanetType(),
                                        game.getCurrentTurn(),
                                        game.getSaveDate()
                                );
                                savedGames.add(saveInfo);
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
                               LocalDateTime saveDate) {

        @Override
        public String toString() {
            return colonyName + " (" + planetType + ") - Turn " + turn + " - " +
                    saveDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }
}