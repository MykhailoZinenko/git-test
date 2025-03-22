package com.colonygenesis.core;

public class Game {
    private boolean initialized = false;
    private boolean running = false;
    private boolean paused = false;

    public Game() {
    }

    public void initialize() {
        this.initialized = true;
    }

    public void start() {
        if (!initialized) {
            initialize();
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

    public boolean isInitialized() {
        return initialized;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isPaused() {
        return paused;
    }
}