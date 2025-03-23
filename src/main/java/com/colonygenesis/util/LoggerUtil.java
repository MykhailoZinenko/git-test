package com.colonygenesis.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.*;

/**
 * Utility class for managing loggers across the application.
 * Provides centralized configuration and access to loggers.
 */
public class LoggerUtil {
    private static final String LOG_FOLDER = "logs";
    private static final String LOG_FILE = "colony-genesis.log";
    private static boolean initialized = false;

    private LoggerUtil() {}

    /**
     * Initializes the logging system.
     * Creates log directory and configures handlers.
     */
    public static synchronized void initialize() {
        if (initialized) {
            return;
        }

        try {
            Path logDir = Paths.get(LOG_FOLDER);
            if (!Files.exists(logDir)) {
                Files.createDirectories(logDir);
            }

            FileHandler fileHandler = new FileHandler(LOG_FOLDER + "/" + LOG_FILE, true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.ALL);

            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter());
            consoleHandler.setLevel(Level.INFO);

            Logger rootLogger = Logger.getLogger("");
            rootLogger.setLevel(Level.INFO);

            Handler[] handlers = rootLogger.getHandlers();
            for (Handler handler : handlers) {
                rootLogger.removeHandler(handler);
            }

            rootLogger.addHandler(fileHandler);
            rootLogger.addHandler(consoleHandler);

            initialized = true;

            Logger.getLogger(LoggerUtil.class.getName()).info("Logging system initialized");
        } catch (IOException e) {
            System.err.println("Failed to initialize logging: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gets a logger for the specified class.
     *
     * @param clazz The class to get the logger for
     * @return A logger for the specified class
     */
    public static Logger getLogger(Class<?> clazz) {
        if (!initialized) {
            initialize();
        }
        return Logger.getLogger(clazz.getName());
    }

    /**
     * Sets the global logging level.
     *
     * @param level The new logging level
     */
    public static void setGlobalLevel(Level level) {
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(level);

        for (Handler handler : rootLogger.getHandlers()) {
            handler.setLevel(level);
        }
    }

    /**
     * Logs a debug message.
     *
     * @param clazz The class originating the log message
     * @param message The message to log
     */
    public static void debug(Class<?> clazz, String message) {
        getLogger(clazz).fine(message);
    }

    /**
     * Logs an info message.
     *
     * @param clazz The class originating the log message
     * @param message The message to log
     */
    public static void info(Class<?> clazz, String message) {
        getLogger(clazz).info(message);
    }

    /**
     * Logs a warning message.
     *
     * @param clazz The class originating the log message
     * @param message The message to log
     */
    public static void warning(Class<?> clazz, String message) {
        getLogger(clazz).warning(message);
    }

    /**
     * Logs an error message.
     *
     * @param clazz The class originating the log message
     * @param message The message to log
     */
    public static void error(Class<?> clazz, String message) {
        getLogger(clazz).severe(message);
    }

    /**
     * Logs an error message with an exception.
     *
     * @param clazz The class originating the log message
     * @param message The message to log
     * @param throwable The exception to log
     */
    public static void error(Class<?> clazz, String message, Throwable throwable) {
        getLogger(clazz).log(Level.SEVERE, message, throwable);
    }
}