package com.colonygenesis.ui.events;

import com.colonygenesis.util.LoggerUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Central event bus for publishing and subscribing to events.
 * Implements the singleton pattern for global access.
 */
public class EventBus {
    private static final Logger LOGGER = LoggerUtil.getLogger(EventBus.class);
    private static EventBus instance;

    private final Map<Class<? extends GameEvent>, List<EventHandler<? extends GameEvent>>> subscribers;

    /**
     * Private constructor to prevent instantiation.
     */
    private EventBus() {
        subscribers = new ConcurrentHashMap<>();
    }

    /**
     * Gets the singleton instance of the EventBus.
     *
     * @return The EventBus instance
     */
    public static synchronized EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }

    /**
     * Registers a subscriber for a specific event type.
     *
     * @param <T> The event type
     * @param eventType The class of the event to subscribe to
     * @param handler The handler to invoke when the event occurs
     */
    public <T extends GameEvent> void subscribe(Class<T> eventType, EventHandler<T> handler) {
        LOGGER.fine("Subscribing to event: " + eventType.getSimpleName());

        subscribers.computeIfAbsent(eventType, k -> new ArrayList<>())
                .add(handler);
    }

    /**
     * Unregisters a subscriber for a specific event type.
     *
     * @param <T> The event type
     * @param eventType The class of the event to unsubscribe from
     * @param handler The handler to unregister
     */
    public <T extends GameEvent> void unsubscribe(Class<T> eventType, EventHandler<T> handler) {
        LOGGER.fine("Unsubscribing from event: " + eventType.getSimpleName());

        if (subscribers.containsKey(eventType)) {
            subscribers.get(eventType).remove(handler);

            if (subscribers.get(eventType).isEmpty()) {
                subscribers.remove(eventType);
            }
        }
    }

    /**
     * Publishes an event to all subscribers.
     *
     * @param <T> The event type
     * @param event The event to publish
     */
    @SuppressWarnings("unchecked")
    public <T extends GameEvent> void publish(T event) {
        LOGGER.fine("Publishing event: " + event.getClass().getSimpleName());

        if (subscribers.containsKey(event.getClass())) {
            for (EventHandler handler : subscribers.get(event.getClass())) {
                try {
                    handler.handle(event);
                } catch (Exception e) {
                    LOGGER.warning("Error handling event: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Clears all subscribers.
     * Mainly used for testing or when shutting down the application.
     */
    public void reset() {
        LOGGER.info("Resetting EventBus - clearing all subscribers");
        subscribers.clear();
    }

    /**
     * Unsubscribes all event handlers registered by a specific object.
     *
     * @param subscriber The subscriber object to unsubscribe
     */
    public void unsubscribeAll(Object subscriber) {
        LOGGER.info("Unsubscribing all events for: " + subscriber.getClass().getSimpleName());

        for (Map.Entry<Class<? extends GameEvent>, List<EventHandler<? extends GameEvent>>> entry : subscribers.entrySet()) {
            List<EventHandler<? extends GameEvent>> handlers = entry.getValue();
            handlers.removeIf(handler -> {
                return handler.toString().contains(subscriber.getClass().getName());
            });

            if (handlers.isEmpty()) {
                subscribers.remove(entry.getKey());
            }
        }
    }
}