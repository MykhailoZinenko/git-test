package com.colonygenesis.ui.events;

import com.colonygenesis.building.AbstractBuilding;
import com.colonygenesis.map.Tile;

/**
 * Event classes related to buildings.
 */
public class BuildingEvents {

    /**
     * Event fired when a building is placed.
     */
    public static class BuildingPlacedEvent implements GameEvent {
        private final AbstractBuilding building;
        private final Tile tile;

        /**
         * Creates a new building placed event.
         *
         * @param building The building that was placed
         * @param tile The tile where the building was placed
         */
        public BuildingPlacedEvent(AbstractBuilding building, Tile tile) {
            this.building = building;
            this.tile = tile;
        }

        /**
         * Gets the building.
         *
         * @return The building
         */
        public AbstractBuilding getBuilding() {
            return building;
        }

        /**
         * Gets the tile.
         *
         * @return The tile
         */
        public Tile getTile() {
            return tile;
        }

        @Override
        public String getName() {
            return "BuildingPlaced";
        }
    }

    /**
     * Event fired when building construction advances.
     */
    public static class BuildingConstructionProgressEvent implements GameEvent {
        private final AbstractBuilding building;
        private final int previousProgress;
        private final int newProgress;

        /**
         * Creates a new building construction progress event.
         *
         * @param building The building under construction
         * @param previousProgress The previous construction progress
         * @param newProgress The new construction progress
         */
        public BuildingConstructionProgressEvent(AbstractBuilding building, int previousProgress, int newProgress) {
            this.building = building;
            this.previousProgress = previousProgress;
            this.newProgress = newProgress;
        }

        /**
         * Gets the building.
         *
         * @return The building
         */
        public AbstractBuilding getBuilding() {
            return building;
        }

        /**
         * Gets the previous progress.
         *
         * @return The previous construction progress
         */
        public int getPreviousProgress() {
            return previousProgress;
        }

        /**
         * Gets the new progress.
         *
         * @return The new construction progress
         */
        public int getNewProgress() {
            return newProgress;
        }

        @Override
        public String getName() {
            return "BuildingConstructionProgress";
        }
    }

    /**
     * Event fired when a building is completed.
     */
    public static class BuildingCompletedEvent implements GameEvent {
        private final AbstractBuilding building;

        /**
         * Creates a new building completed event.
         *
         * @param building The building that was completed
         */
        public BuildingCompletedEvent(AbstractBuilding building) {
            this.building = building;
        }

        /**
         * Gets the building.
         *
         * @return The building
         */
        public AbstractBuilding getBuilding() {
            return building;
        }

        /**
         * Gets the tile where the building is located.
         *
         * @return The tile
         */
        public Tile getTile() {
            return building.getLocation();
        }

        @Override
        public String getName() {
            return "BuildingCompleted";
        }
    }

    /**
     * Event fired when a building is activated.
     */
    public static class BuildingActivatedEvent implements GameEvent {
        private final AbstractBuilding building;
        private final int efficiency;

        /**
         * Creates a new building activated event.
         *
         * @param building The building that was activated
         * @param efficiency The efficiency percentage of the building
         */
        public BuildingActivatedEvent(AbstractBuilding building, int efficiency) {
            this.building = building;
            this.efficiency = efficiency;
        }

        /**
         * Gets the building.
         *
         * @return The building
         */
        public AbstractBuilding getBuilding() {
            return building;
        }

        /**
         * Gets the efficiency percentage.
         *
         * @return The efficiency percentage
         */
        public int getEfficiency() {
            return efficiency;
        }

        /**
         * Gets the tile where the building is located.
         *
         * @return The tile
         */
        public Tile getTile() {
            return building.getLocation();
        }

        @Override
        public String getName() {
            return "BuildingActivated";
        }
    }

    /**
     * Event fired when a building is deactivated.
     */
    public static class BuildingDeactivatedEvent implements GameEvent {
        private final AbstractBuilding building;

        /**
         * Creates a new building deactivated event.
         *
         * @param building The building that was deactivated
         */
        public BuildingDeactivatedEvent(AbstractBuilding building) {
            this.building = building;
        }

        /**
         * Gets the building.
         *
         * @return The building
         */
        public AbstractBuilding getBuilding() {
            return building;
        }

        /**
         * Gets the tile where the building is located.
         *
         * @return The tile
         */
        public Tile getTile() {
            return building.getLocation();
        }

        @Override
        public String getName() {
            return "BuildingDeactivated";
        }
    }

    /**
     * Event fired when workers are assigned to a building.
     */
    public static class WorkersAssignedEvent implements GameEvent {
        private final AbstractBuilding building;
        private final int previousWorkers;
        private final int newWorkers;

        /**
         * Creates a new workers assigned event.
         *
         * @param building The building that workers were assigned to
         * @param previousWorkers The previous number of workers
         * @param newWorkers The new number of workers
         */
        public WorkersAssignedEvent(AbstractBuilding building, int previousWorkers, int newWorkers) {
            this.building = building;
            this.previousWorkers = previousWorkers;
            this.newWorkers = newWorkers;
        }

        /**
         * Gets the building.
         *
         * @return The building
         */
        public AbstractBuilding getBuilding() {
            return building;
        }

        /**
         * Gets the previous number of workers.
         *
         * @return The previous worker count
         */
        public int getPreviousWorkers() {
            return previousWorkers;
        }

        /**
         * Gets the new number of workers.
         *
         * @return The new worker count
         */
        public int getNewWorkers() {
            return newWorkers;
        }

        /**
         * Gets the change in workers.
         *
         * @return The change in worker count
         */
        public int getDelta() {
            return newWorkers - previousWorkers;
        }

        @Override
        public String getName() {
            return "WorkersAssigned";
        }
    }
}