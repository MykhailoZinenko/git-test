package com.colonygenesis.ui.events;

/**
 * Interface for handling game events.
 *
 * @param <T> The type of event to handle
 */
@FunctionalInterface
public interface EventHandler<T extends GameEvent> {

    /**
     * Handles the event.
     *
     * @param event The event to handle
     */
    void handle(T event);
}