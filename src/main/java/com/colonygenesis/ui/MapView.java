package com.colonygenesis.ui;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * Map view component that displays the game world as a hexagonal grid.
 * Supports panning and zooming functionality.
 */
public class MapView extends Pane {
    private final Canvas canvas;
    private final GraphicsContext gc;

    private final int mapWidth = 30;
    private final int mapHeight = 20;
    private final double hexSize = 30.0;

    private double translateX = 0;
    private double translateY = 0;
    private double scale = 1.0;

    private double lastMouseX;
    private double lastMouseY;
    private boolean isDragging = false;

    /**
     * Constructs a new map view and initializes the UI components.
     */
    public MapView() {
        canvas = new Canvas();
        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());
        getChildren().add(canvas);

        gc = canvas.getGraphicsContext2D();

        setOnMousePressed(this::handleMousePressed);
        setOnMouseDragged(this::handleMouseDragged);
        setOnMouseReleased(this::handleMouseReleased);
        setOnScroll(this::handleScroll);

        widthProperty().addListener((obs, oldVal, newVal) -> draw());
        heightProperty().addListener((obs, oldVal, newVal) -> draw());

        Platform.runLater(this::resetView);
    }

    /**
     * Handles mouse press events.
     *
     * @param event The mouse event
     */
    private void handleMousePressed(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            lastMouseX = event.getX();
            lastMouseY = event.getY();
            isDragging = true;
        }
    }

    /**
     * Handles mouse drag events.
     *
     * @param event The mouse event
     */
    private void handleMouseDragged(MouseEvent event) {
        if (isDragging) {
            double dx = event.getX() - lastMouseX;
            double dy = event.getY() - lastMouseY;

            translateX += dx;
            translateY += dy;

            lastMouseX = event.getX();
            lastMouseY = event.getY();

            draw();
        }
    }

    /**
     * Handles mouse release events.
     *
     * @param event The mouse event
     */
    private void handleMouseReleased(MouseEvent event) {
        isDragging = false;
    }

    /**
     * Handles scroll events for zooming.
     *
     * @param event The scroll event
     */
    private void handleScroll(ScrollEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY();

        double zoomFactor = 1.05;
        double deltaY = event.getDeltaY();

        if (deltaY < 0) {
            zoomFactor = 1 / zoomFactor;
        }

        double newScale = scale * zoomFactor;
        if (newScale < 0.3) newScale = 0.3;
        if (newScale > 3.0) newScale = 3.0;

        if (newScale == scale) return;

        double worldX = (mouseX - translateX) / scale;
        double worldY = (mouseY - translateY) / scale;

        translateX = mouseX - worldX * newScale;
        translateY = mouseY - worldY * newScale;

        scale = newScale;
        draw();

        event.consume();
    }

    /**
     * Draws the map view.
     */
    public void draw() {
        if (getWidth() <= 0 || getHeight() <= 0) return;

        gc.setFill(Color.rgb(20, 20, 30));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.save();
        gc.translate(translateX, translateY);
        gc.scale(scale, scale);

        drawHexGrid();

        gc.restore();

        gc.setStroke(Color.DARKGREY);
        gc.setLineWidth(1);
        gc.strokeRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Draws the hexagonal grid.
     */
    private void drawHexGrid() {
        double width = mapWidth * hexSize * 1.5;
        double height = mapHeight * hexSize * Math.sqrt(3);

        gc.setFill(Color.rgb(30, 30, 40, 0.3));
        gc.fillRect(0, 0, width, height);

        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                drawHexagon(x, y);
            }
        }

        gc.setFill(Color.WHITE);
        gc.fillText("Map Size: " + mapWidth + "x" + mapHeight, 10, 20);
    }

    /**
     * Draws a hexagon at the specified grid coordinates.
     *
     * @param gridX The grid x coordinate
     * @param gridY The grid y coordinate
     */
    private void drawHexagon(int gridX, int gridY) {
        double centerX = gridX * hexSize * 1.5;
        double centerY = gridY * hexSize * Math.sqrt(3);

        if (gridX % 2 == 1) {
            centerY += hexSize * Math.sqrt(3) / 2;
        }

        double[] xPoints = new double[6];
        double[] yPoints = new double[6];

        for (int i = 0; i < 6; i++) {
            double angle = 2 * Math.PI / 6 * i;
            xPoints[i] = centerX + hexSize * Math.cos(angle);
            yPoints[i] = centerY + hexSize * Math.sin(angle);
        }

        gc.setFill(Color.rgb(50, 50, 70));
        gc.setStroke(Color.rgb(80, 80, 100));
        gc.setLineWidth(1);

        gc.beginPath();
        gc.moveTo(xPoints[0], yPoints[0]);

        for (int i = 1; i < 6; i++) {
            gc.lineTo(xPoints[i], yPoints[i]);
        }

        gc.closePath();
        gc.fill();
        gc.stroke();

        if (scale > 1.5) {
            gc.setFill(Color.WHITE);
            gc.fillText(gridX + "," + gridY, centerX - 10, centerY + 5);
        }
    }

    /**
     * Resets the view to center the map and reset zoom.
     */
    public void resetView() {
        translateX = getWidth() / 2 - (mapWidth * hexSize * 1.5) / 2;
        translateY = getHeight() / 2 - (mapHeight * hexSize * Math.sqrt(3)) / 2;
        scale = 1.0;
        draw();
    }

    /**
     * Handles resizing of the view.
     * Overrides the resize method of the parent but doesn't attempt to
     * directly set canvas dimensions since they're bound in the constructor.
     *
     * @param width The new width
     * @param height The new height
     */
    @Override
    public void resize(double width, double height) {
        super.resize(width, height);
        draw();
    }
}