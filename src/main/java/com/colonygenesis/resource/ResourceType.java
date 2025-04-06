package com.colonygenesis.resource;

import javafx.scene.paint.Color;

/**
 * Enumeration of resource types in the game.
 * Each type has unique properties and uses.
 */
public enum ResourceType {
    FOOD("Food", "Sustains your colony population", Color.GREEN, true, true),
    ENERGY("Energy", "Powers buildings and operations", Color.YELLOW, true, false),
    MATERIALS("Materials", "Used for construction and maintenance", Color.BROWN, true, true),
    WATER("Water", "Essential for life support and agriculture", Color.LIGHTBLUE, true, true),
    RESEARCH("Research", "Advances technology", Color.PURPLE, true, false),
    RARE_MINERALS("Rare Minerals", "Advanced construction material", Color.SILVER, false, true),
    ALIEN_COMPOUNDS("Alien Compounds", "Mysterious alien substances", Color.MAGENTA, false, true),
    POPULATION("Population", "Colonists who operate buildings", Color.rgb(230, 180, 180), true, true, true);

    private final String name;
    private final String description;
    private final Color color;
    private final boolean basic;
    private final boolean storable;
    private final int baseStorage;

    /**
     * Constructs a resource type with specified properties.
     *
     * @param name The name of the resource
     * @param description A short description of the resource
     * @param color The color used to represent the resource in the UI
     * @param basic Whether this is a basic resource
     * @param storable Whether this resource can be stored
     */
    ResourceType(String name, String description, Color color, boolean basic, boolean storable, boolean isPopulation) {
        this.name = name;
        this.description = description;
        this.color = color;
        this.basic = basic;
        this.storable = storable;

        if (storable) {
            if (isPopulation) {
                this.baseStorage = 10; // Default population capacity
            } else if (basic) {
                this.baseStorage = 1000;
            } else {
                this.baseStorage = 500;
            }
        } else {
            this.baseStorage = 0;
        }
    }

    ResourceType(String name, String description, Color color, boolean basic, boolean storable) {
        this(name, description, color, basic, storable, false);
    }

    /**
     * Gets the name of the resource.
     *
     * @return The resource name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of the resource.
     *
     * @return The resource description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the color associated with the resource.
     *
     * @return The resource color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Checks if this is a basic resource.
     * Basic resources are easier to produce and essential for colony operation.
     *
     * @return true if this is a basic resource, false otherwise
     */
    public boolean isBasic() {
        return basic;
    }

    /**
     * Checks if this resource can be stored.
     * Non-storable resources are used or lost each turn.
     *
     * @return true if this resource can be stored, false otherwise
     */
    public boolean isStorable() {
        return storable;
    }

    /**
     * Gets the base storage capacity for this resource.
     *
     * @return The base storage capacity
     */
    public int getBaseStorage() {
        return baseStorage;
    }

    /**
     * Gets the building type that increases storage for this resource.
     *
     * @return The name of the storage building
     */
    public String getStorageBuilding() {
        return switch (this) {
            case FOOD -> "Food Silo";
            case MATERIALS -> "Warehouse";
            case WATER -> "Water Tank";
            case RARE_MINERALS -> "Secure Vault";
            case ALIEN_COMPOUNDS -> "Containment Facility";
            case POPULATION -> "Habitation";
            default -> "No storage building";
        };
    }

    /**
     * Checks if this resource is population.
     *
     * @return true if this is the population resource, false otherwise
     */
    public boolean isPopulation() {
        return this.name.equals("Population");
    }
}