package com.colonygenesis.ui.events;

import com.colonygenesis.resource.ResourceType;
import java.util.Map;

/**
 * Event classes related to resource management.
 */
public class ResourceEvents {

    /**
     * Event fired when a resource amount changes.
     */
    public static class ResourceChangedEvent implements GameEvent {
        private final ResourceType resourceType;
        private final int newAmount;
        private final int previousAmount;
        private final boolean isProduction;

        /**
         * Creates a new resource changed event.
         *
         * @param resourceType The type of resource that changed
         * @param newAmount The new amount of the resource
         * @param previousAmount The previous amount of the resource
         * @param isProduction Whether this is a production update (vs consumption)
         */
        public ResourceChangedEvent(ResourceType resourceType, int newAmount, int previousAmount, boolean isProduction) {
            this.resourceType = resourceType;
            this.newAmount = newAmount;
            this.previousAmount = previousAmount;
            this.isProduction = isProduction;
        }

        /**
         * Creates a new resource changed event (non-production).
         *
         * @param resourceType The type of resource that changed
         * @param newAmount The new amount of the resource
         * @param previousAmount The previous amount of the resource
         */
        public ResourceChangedEvent(ResourceType resourceType, int newAmount, int previousAmount) {
            this(resourceType, newAmount, previousAmount, false);
        }

        /**
         * Gets the resource type.
         *
         * @return The resource type
         */
        public ResourceType getResourceType() {
            return resourceType;
        }

        /**
         * Gets the new amount.
         *
         * @return The new resource amount
         */
        public int getNewAmount() {
            return newAmount;
        }

        /**
         * Gets the previous amount.
         *
         * @return The previous resource amount
         */
        public int getPreviousAmount() {
            return previousAmount;
        }

        /**
         * Gets the change in amount.
         *
         * @return The change in resource amount
         */
        public int getDelta() {
            return newAmount - previousAmount;
        }

        /**
         * Checks if this is a production update.
         *
         * @return true if this is a production update, false otherwise
         */
        public boolean isProduction() {
            return isProduction;
        }

        @Override
        public String getName() {
            return "ResourceChanged";
        }
    }

    /**
     * Event fired when all resources are updated at once.
     */
    public static class ResourcesUpdatedEvent implements GameEvent {
        private final Map<ResourceType, Integer> resources;
        private final Map<ResourceType, Integer> production;
        private final Map<ResourceType, Integer> capacity;

        /**
         * Creates a new resources updated event.
         *
         * @param resources The current resource amounts
         * @param production The current production rates
         * @param capacity The current storage capacities
         */
        public ResourcesUpdatedEvent(Map<ResourceType, Integer> resources,
                                     Map<ResourceType, Integer> production,
                                     Map<ResourceType, Integer> capacity) {
            this.resources = resources;
            this.production = production;
            this.capacity = capacity;
        }

        /**
         * Gets the current resource amounts.
         *
         * @return The resource amounts
         */
        public Map<ResourceType, Integer> getResources() {
            return resources;
        }

        /**
         * Gets the current production rates.
         *
         * @return The production rates
         */
        public Map<ResourceType, Integer> getProduction() {
            return production;
        }

        /**
         * Gets the current storage capacities.
         *
         * @return The storage capacities
         */
        public Map<ResourceType, Integer> getCapacity() {
            return capacity;
        }

        @Override
        public String getName() {
            return "ResourcesUpdated";
        }
    }

    /**
     * Event fired when a resource shortage occurs.
     */
    public static class ResourceShortageEvent implements GameEvent {
        private final ResourceType resourceType;
        private final int shortageAmount;

        /**
         * Creates a new resource shortage event.
         *
         * @param resourceType The type of resource with a shortage
         * @param shortageAmount The amount of the shortage
         */
        public ResourceShortageEvent(ResourceType resourceType, int shortageAmount) {
            this.resourceType = resourceType;
            this.shortageAmount = shortageAmount;
        }

        /**
         * Gets the resource type.
         *
         * @return The resource type
         */
        public ResourceType getResourceType() {
            return resourceType;
        }

        /**
         * Gets the shortage amount.
         *
         * @return The shortage amount
         */
        public int getShortageAmount() {
            return shortageAmount;
        }

        @Override
        public String getName() {
            return "ResourceShortage";
        }
    }
}