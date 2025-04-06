package com.colonygenesis.ui.events;

/**
 * Event classes related to colony management.
 */
public class ColonyEvents {

    /**
     * Event fired when the population changes.
     */
    public static class PopulationChangedEvent implements GameEvent {
        private final int totalPopulation;
        private final int housingCapacity;
        private final int availableWorkers;
        private final int previousPopulation;

        /**
         * Creates a new population changed event.
         *
         * @param totalPopulation The new total population
         * @param housingCapacity The current housing capacity
         * @param availableWorkers The number of available workers
         * @param previousPopulation The previous total population
         */
        public PopulationChangedEvent(int totalPopulation, int housingCapacity,
                                      int availableWorkers, int previousPopulation) {
            this.totalPopulation = totalPopulation;
            this.housingCapacity = housingCapacity;
            this.availableWorkers = availableWorkers;
            this.previousPopulation = previousPopulation;
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
         * Gets the housing capacity.
         *
         * @return The housing capacity
         */
        public int getHousingCapacity() {
            return housingCapacity;
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
         * Gets the previous total population.
         *
         * @return The previous population
         */
        public int getPreviousPopulation() {
            return previousPopulation;
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

    /**
     * Event fired when housing capacity changes.
     */
    public static class HousingCapacityChangedEvent implements GameEvent {
        private final int housingCapacity;
        private final int previousCapacity;

        /**
         * Creates a new housing capacity changed event.
         *
         * @param housingCapacity The new housing capacity
         * @param previousCapacity The previous housing capacity
         */
        public HousingCapacityChangedEvent(int housingCapacity, int previousCapacity) {
            this.housingCapacity = housingCapacity;
            this.previousCapacity = previousCapacity;
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
         * Gets the previous housing capacity.
         *
         * @return The previous capacity
         */
        public int getPreviousCapacity() {
            return previousCapacity;
        }

        /**
         * Gets the change in capacity.
         *
         * @return The capacity change
         */
        public int getDelta() {
            return housingCapacity - previousCapacity;
        }

        @Override
        public String getName() {
            return "HousingCapacityChanged";
        }
    }

    /**
     * Event fired when worker availability changes.
     */
    public static class WorkerAvailabilityChangedEvent implements GameEvent {
        private final int availableWorkers;
        private final int previousAvailableWorkers;

        /**
         * Creates a new worker availability changed event.
         *
         * @param availableWorkers The new number of available workers
         * @param previousAvailableWorkers The previous number of available workers
         */
        public WorkerAvailabilityChangedEvent(int availableWorkers, int previousAvailableWorkers) {
            this.availableWorkers = availableWorkers;
            this.previousAvailableWorkers = previousAvailableWorkers;
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
         * Gets the change in available workers.
         *
         * @return The worker availability change
         */
        public int getDelta() {
            return availableWorkers - previousAvailableWorkers;
        }

        @Override
        public String getName() {
            return "WorkerAvailabilityChanged";
        }
    }
}