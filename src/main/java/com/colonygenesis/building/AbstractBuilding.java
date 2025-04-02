package com.colonygenesis.building;

import com.colonygenesis.map.Tile;
import com.colonygenesis.resource.ResourceType;
import com.colonygenesis.ui.events.BuildingEvents;
import com.colonygenesis.ui.events.EventBus;
import com.colonygenesis.util.LoggerUtil;

import java.io.Serial;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Abstract base class for all buildings in the game.
 * Defines common building properties and behavior.
 */
public abstract class AbstractBuilding implements Serializable {
    protected static final Logger LOGGER = LoggerUtil.getLogger(AbstractBuilding.class);
    @Serial
    private static final long serialVersionUID = 1L;

    protected String name;
    protected String description;
    protected Tile location;
    protected Map<ResourceType, Integer> constructionCost;
    protected Map<ResourceType, Integer> maintenanceCost;
    protected int constructionTime;
    protected int remainingConstructionTime;
    protected boolean active;
    protected int workersRequired;
    protected int workersAssigned;
    protected BuildingType buildingType;

    // Event bus for publishing events
    private final transient EventBus eventBus;

    /**
     * Constructs a new building.
     *
     * @param name The name of the building
     * @param description A brief description of the building
     * @param location The tile where the building is located
     * @param constructionTime The number of turns to construct the building
     * @param workersRequired The number of workers required to operate the building
     * @param buildingType The type of building
     */
    public AbstractBuilding(String name, String description, Tile location,
                            int constructionTime, int workersRequired,
                            BuildingType buildingType) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.constructionTime = constructionTime;
        this.remainingConstructionTime = constructionTime;
        this.workersRequired = workersRequired;
        this.workersAssigned = 0;
        this.buildingType = buildingType;
        this.active = false;

        this.constructionCost = new EnumMap<>(ResourceType.class);
        this.maintenanceCost = new EnumMap<>(ResourceType.class);

        this.eventBus = EventBus.getInstance();

        // Don't call these abstract methods from constructor - subclasses handle initialization

        LOGGER.fine("Created building: " + name + " at " + location);
    }

    /**
     * Initialize the construction cost of the building.
     * Can be overridden by subclasses, but initialization should preferably be done in constructors.
     */
    protected void initializeConstructionCost() {
        // Default empty implementation
    }

    /**
     * Initialize the maintenance cost of the building.
     * Can be overridden by subclasses, but initialization should preferably be done in constructors.
     */
    protected void initializeMaintenanceCost() {
        // Default empty implementation
    }

    /**
     * Process the building's operation for the current turn.
     * This is where the building produces resources, affects population, etc.
     * To be implemented by subclasses.
     *
     * @return A map of resources produced/consumed by this building's operation
     */
    public abstract Map<ResourceType, Integer> operate();

    /**
     * Progress the building's construction by one turn.
     *
     * @return true if construction is complete, false otherwise
     */
    public boolean progressConstruction() {
        if (isComplete()) {
            return true;
        }

        int previousProgress = getConstructionProgress();
        remainingConstructionTime--;
        int newProgress = getConstructionProgress();

        LOGGER.fine("Construction progress for " + name + ": " +
                (constructionTime - remainingConstructionTime) + "/" + constructionTime);

        // Publish construction progress event
        eventBus.publish(new BuildingEvents.BuildingConstructionProgressEvent(this, previousProgress, newProgress));

        if (remainingConstructionTime <= 0) {
            LOGGER.info("Building " + name + " at " + location + " completed");

            // Publish building completed event
            eventBus.publish(new BuildingEvents.BuildingCompletedEvent(this));

            return true;
        }

        return false;
    }

    /**
     * Checks if the building construction is complete.
     *
     * @return true if complete, false otherwise
     */
    public boolean isComplete() {
        return remainingConstructionTime <= 0;
    }

    /**
     * Activates the building if it has sufficient workers.
     * Buildings with at least one worker will operate at reduced efficiency.
     *
     * @return true if activated, false otherwise
     */
    public boolean activate() {
        if (isComplete()) {
            // Can activate if:
            // 1. No workers required, or
            // 2. At least one worker assigned
            if (workersRequired == 0 || workersAssigned > 0) {
                active = true;
                int efficiency = calculateEfficiency();

                LOGGER.info("Building " + name + " at " + location + " activated" +
                        (efficiency < 100 ? " at " + efficiency + "% efficiency" : ""));

                // Publish activation event
                eventBus.publish(new BuildingEvents.BuildingActivatedEvent(this, efficiency));

                return true;
            } else {
                LOGGER.warning("Cannot activate " + name + ": no workers assigned");
            }
        } else {
            LOGGER.warning("Cannot activate " + name + ": construction not complete");
        }

        return false;
    }

    /**
     * Deactivates the building.
     */
    public void deactivate() {
        if (active) {
            active = false;
            LOGGER.info("Building " + name + " at " + location + " deactivated");

            // Publish deactivation event
            eventBus.publish(new BuildingEvents.BuildingDeactivatedEvent(this));
        }
    }

    /**
     * Assigns workers to this building.
     * Will activate the building if it was inactive and now has workers.
     *
     * @param workers The number of workers to assign
     * @return The number of workers actually assigned
     */
    public int assignWorkers(int workers) {
        int previousWorkers = workersAssigned;
        int availableSlots = workersRequired - workersAssigned;
        int workersToAssign = Math.min(workers, availableSlots);

        workersAssigned += workersToAssign;
        LOGGER.fine("Assigned " + workersToAssign + " workers to " + name +
                " (" + workersAssigned + "/" + workersRequired + ")");

        // Publish worker assignment event
        eventBus.publish(new BuildingEvents.WorkersAssignedEvent(this, previousWorkers, workersAssigned));

        // Activate if completed and either:
        // 1. Previously had no workers but now has at least one, or
        // 2. Was already active (just changing efficiency)
        if (isComplete() && ((previousWorkers == 0 && workersAssigned > 0) || active)) {
            activate();
        }

        return workersToAssign;
    }

    /**
     * Removes workers from this building.
     * Will deactivate the building if it has no workers left and requires workers.
     *
     * @param workers The number of workers to remove
     * @return The number of workers actually removed
     */
    public int removeWorkers(int workers) {
        int previousWorkers = workersAssigned;
        int workersToRemove = Math.min(workers, workersAssigned);

        workersAssigned -= workersToRemove;
        LOGGER.fine("Removed " + workersToRemove + " workers from " + name +
                " (" + workersAssigned + "/" + workersRequired + ")");

        // Publish worker assignment event
        eventBus.publish(new BuildingEvents.WorkersAssignedEvent(this, previousWorkers, workersAssigned));

        // Deactivate if it requires workers but now has none
        if (workersRequired > 0 && workersAssigned == 0 && active) {
            deactivate();
        }
        // Otherwise, if still active, update the efficiency
        else if (active) {
            int efficiency = calculateEfficiency();
            eventBus.publish(new BuildingEvents.BuildingActivatedEvent(this, efficiency));
        }

        return workersToRemove;
    }

    /**
     * Calculates the building's operational efficiency based on worker assignment.
     *
     * @return The efficiency percentage (0-100)
     */
    public int calculateEfficiency() {
        if (workersRequired == 0) return 100; // No workers needed = 100% efficiency
        return Math.min(100, (workersAssigned * 100) / workersRequired);
    }

    // Getters and setters

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Tile getLocation() {
        return location;
    }

    public Map<ResourceType, Integer> getConstructionCost() {
        return new EnumMap<>(constructionCost);
    }

    public Map<ResourceType, Integer> getMaintenanceCost() {
        return new EnumMap<>(maintenanceCost);
    }

    public int getConstructionTime() {
        return constructionTime;
    }

    public int getRemainingConstructionTime() {
        return remainingConstructionTime;
    }

    public boolean isActive() {
        return active;
    }

    public int getWorkersRequired() {
        return workersRequired;
    }

    public int getWorkersAssigned() {
        return workersAssigned;
    }

    public BuildingType getBuildingType() {
        return buildingType;
    }

    /**
     * Returns the construction progress as a percentage.
     *
     * @return The construction progress (0-100)
     */
    public int getConstructionProgress() {
        if (constructionTime == 0) return 100;
        return (int)(((double)(constructionTime - remainingConstructionTime) / constructionTime) * 100);
    }

    @Override
    public String toString() {
        return name + " at " + location + " (" + (isComplete() ? "Complete" :
                getConstructionProgress() + "% built") + ", " +
                (active ? "Active" : "Inactive") + ")";
    }
}