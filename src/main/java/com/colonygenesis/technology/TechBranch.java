package com.colonygenesis.technology;

/**
 * Enumeration of technology branches.
 */
public enum TechBranch {
    SURVIVAL("Survival", javafx.scene.paint.Color.rgb(0, 128, 0)),
    INDUSTRY("Industry", javafx.scene.paint.Color.rgb(255, 140, 0)),
    SCIENCE("Science", javafx.scene.paint.Color.rgb(0, 0, 255)),
    ADAPTATION("Adaptation", javafx.scene.paint.Color.rgb(128, 0, 128));

    private final String name;
    private final javafx.scene.paint.Color color;

    TechBranch(String name, javafx.scene.paint.Color color) {
        this.name = name;
        this.color = color;
    }

    public String getName() { return name; }
    public javafx.scene.paint.Color getColor() { return color; }
}
