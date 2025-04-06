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
        private final int availableWorkers;
        private final int assignedWorkers;

        /**
         * Creates a new resources updated event.
         *
         * @param resources The current resource amounts
         * @param production The current production rates
         * @param capacity The current storage capacities
         * @param availableWorkers The number of available workers
         * @param assignedWorkers The number of assigned workers
         */
        public ResourcesUpdatedEvent(Map<ResourceType, Integer> resources,
                                     Map<ResourceType, Integer> production,
                                     Map<ResourceType, Integer> capacity,
                                     int availableWorkers,
                                     int assignedWorkers) {
            this.resources = resources;
            this.production = production;
            this.capacity = capacity;
            this.availableWorkers = availableWorkers;
            this.assignedWorkers = assignedWorkers;
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

        /**
         * Gets the number of available workers.
         *
         * @return The number of available workers
         */
        public int getAvailableWorkers() {
            return availableWorkers;
        }

        /**
         * Gets the number of assigned workers.
         *
         * @return The number of assigned workers
         */
        public int getAssignedWorkers() {
            return assignedWorkers;
        }

        /**
         * Gets the total population.
         *
         * @return The total population
         */
        public int getTotalPopulation() {
            return resources.getOrDefault(ResourceType.POPULATION, 0);
        }

        /**
         * Gets the housing capacity.
         *
         * @return The housing capacity
         */
        public int getHousingCapacity() {
            return capacity.getOrDefault(ResourceType.POPULATION, 0);
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

    /**
     * Event fired when worker availability changes.
     */
    public static class WorkerAvailabilityChangedEvent implements GameEvent {
        private final int availableWorkers;
        private final int previousAvailableWorkers;
        private final int delta;

        /**
         * Creates a new worker availability changed event.
         *
         * @param availableWorkers The new number of available workers
         * @param previousAvailableWorkers The previous number of available workers
         * @param delta The change in worker assignment (positive: more assigned, negative: fewer assigned)
         */
        public WorkerAvailabilityChangedEvent(int availableWorkers, int previousAvailableWorkers, int delta) {
            this.availableWorkers = availableWorkers;
            this.previousAvailableWorkers = previousAvailableWorkers;
            this.delta = delta;
        }

        /**
         * Gets the number of available workers.
         *
         * @return The available workers
         */
        public int getAvailableWorkers() {
            return availableWorkers;
        }

        /**
         * Gets the previous number of available workers.
         *
         * @return The previous available workers
         */
        public int getPreviousAvailableWorkers() {
            return previousAvailableWorkers;
        }

        /**
         * Gets the change in worker assignment.
         * Positive means more workers were assigned (fewer available).
         * Negative means workers were unassigned (more available).
         *
         * @return The worker assignment change
         */
        public int getDelta() {
            return delta;
        }

        @Override
        public String getName() {
            return "WorkerAvailabilityChanged";
        }
    }

    /**
     * Event fired when population changes (due to growth or other factors).
     */
    public static class PopulationChangedEvent implements GameEvent {
        private final int totalPopulation;
        private final int previousPopulation;
        private final int housingCapacity;

        /**
         * Creates a new population changed event.
         *
         * @param totalPopulation The new total population
         * @param previousPopulation The previous total population
         * @param housingCapacity The current housing capacity
         */
        public PopulationChangedEvent(int totalPopulation, int previousPopulation, int housingCapacity) {
            this.totalPopulation = totalPopulation;
            this.previousPopulation = previousPopulation;
            this.housingCapacity = housingCapacity;
        }

        /**
         * Gets the total population.
         *
         * @return The total population
         */
        public int getTotalPopulation() {
            return totalPopulation;
        }

        /**
         * Gets the previous total population.
         *
         * @return The previous population
         */
        public int getPreviousPopulation() {
            return previousPopulation;
        }

        /**
         * Gets the housing capacity.
         *
         * @return The housing capacity
         */
        public int getHousingCapacity() {
            return housingCapacity;
        }

        /**
         * Gets the change in population.
         *
         * @return The population change
         */
        public int getDelta() {
            return totalPopulation - previousPopulation;
        }

        @Override
        public String getName() {
            return "PopulationChanged";
        }
    }
}