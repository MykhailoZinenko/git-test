package com.colonygenesis.ui.events;

/**
 * Base interface for all game events.
 * All specific event types must implement this interface.
 */
public interface GameEvent {

    /**
     * Gets the name of the event.
     *
     * @return The event name
     */
    String getName();
}