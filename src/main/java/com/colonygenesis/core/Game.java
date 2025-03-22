package com.colonygenesis.core;

import com.colonygenesis.map.PlanetType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Game implements Serializable {
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

    public Game() {}

    public void initialize(String colonyName, PlanetType planetType, int mapSize) {
        this.colonyName = colonyName;
        this.planetType = planetType;
        this.mapSize = mapSize;
        this.currentTurn = 1;
        this.initialized = true;
    }

    public void start() {
        if (!initialized) {
            throw new IllegalStateException("Game must be initialized before starting");
        }
        this.running = true;
        this.paused = false;
    }

    public void pause() {
        this.paused = true;
    }

    public void resume() {
        this.paused = false;
    }

    public void stop() {
        this.running = false;
    }

    public String saveGame() {
        try {
            Path savesDir = Paths.get("saves");
            if (!Files.exists(savesDir)) {
                Files.createDirectories(savesDir);
            }

            this.saveDate = LocalDateTime.now();

            String timestamp = saveDate.format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            String filename = "saves/" + timestamp + "_" + colonyName.replaceAll("\\s+", "_") + ".save";

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
                oos.writeObject(this);
            }

            System.out.println("Game saved to: " + filename);
            return filename;

        } catch (IOException e) {
            System.err.println("Error saving game: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static Game loadGame(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            Game loadedGame = (Game) ois.readObject();
            System.out.println("Game loaded from: " + filename);
            return loadedGame;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading game: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static List<SaveGameInfo> getSavedGames() {
        List<SaveGameInfo> savedGames = new ArrayList<>();
        Path savesDir = Paths.get("saves");

        if (!Files.exists(savesDir)) {
            try {
                Files.createDirectories(savesDir);
            } catch (IOException e) {
                System.err.println("Error creating saves directory: " + e.getMessage());
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
                            System.err.println("Error loading save info from: " + path + " - " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
            System.err.println("Error listing save files: " + e.getMessage());
        }

        return savedGames;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isPaused() {
        return paused;
    }

    public String getColonyName() {
        return colonyName;
    }

    public PlanetType getPlanetType() {
        return planetType;
    }

    public int getMapSize() {
        return mapSize;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(int currentTurn) {
        this.currentTurn = currentTurn;
    }

    public LocalDateTime getSaveDate() {
        return saveDate;
    }

    public record SaveGameInfo(String filename, String colonyName, PlanetType planetType, int turn,
                               LocalDateTime saveDate) {

        @Override
            public String toString() {
                return colonyName + " (" + planetType + ") - Turn " + turn + " - " +
                        saveDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
        }
}