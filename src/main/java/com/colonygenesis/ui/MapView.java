package com.colonygenesis.ui;

import com.colonygenesis.map.HexGrid;
import com.colonygenesis.map.TerrainType;
import com.colonygenesis.map.Tile;
import com.colonygenesis.ui.debug.DebugOverlay;
import com.colonygenesis.ui.events.BuildingEvents;
import com.colonygenesis.ui.events.EventBus;
import com.colonygenesis.ui.events.TileEvents;
import com.colonygenesis.util.LoggerUtil;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.util.logging.Logger;

/**
 * Map view component that displays the game world as a hexagonal grid.
 * Supports panning and zooming functionality.
 */
public class MapView extends Pane {
    private static final Logger LOGGER = LoggerUtil.getLogger(MapView.class);

    private final Canvas canvas;
    private final GraphicsContext gc;
    private HexGrid grid;
    private final EventBus eventBus;

    private final double hexSize = 30.0;

    private double translateX = 0;
    private double translateY = 0;
    private double scale = 1.0;

    private double lastMouseX;
    private double lastMouseY;
    private boolean isDragging = false;

    private long lastRenderTimeNs = 0;
    private int visibleHexagons = 0;
    private int totalHexagons = 0;
    private DebugOverlay debugOverlay;

    private Tile selectedTile = null;
    private Tile hoveredTile = null;

    /**
     * Constructs a new map view and initializes the UI components.
     */
    public MapView() {
        canvas = new Canvas();
        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());
        getChildren().add(canvas);

        gc = canvas.getGraphicsContext2D();
        eventBus = EventBus.getInstance();

        setOnMousePressed(this::handleMousePressed);
        setOnMouseDragged(this::handleMouseDragged);
        setOnMouseReleased(this::handleMouseReleased);
        setOnMouseMoved(this::handleMouseMoved);
        setOnMouseClicked(this::handleMouseClicked);
        setOnScroll(this::handleScroll);

        widthProperty().addListener((obs, oldVal, newVal) -> draw());
        heightProperty().addListener((obs, oldVal, newVal) -> draw());

        initializeEventSubscriptions();
    }

    /**
     * Initializes all event subscriptions for reactive updates.
     */
    private void initializeEventSubscriptions() {
        eventBus.subscribe(TileEvents.TileUpdatedEvent.class, this::handleTileUpdated);
        eventBus.subscribe(TileEvents.RefreshMapEvent.class, this::handleRefreshMap);

        eventBus.subscribe(BuildingEvents.BuildingPlacedEvent.class, event ->
                Platform.runLater(() -> renderTile(event.getTile())));
        eventBus.subscribe(BuildingEvents.BuildingCompletedEvent.class, event ->
                Platform.runLater(() -> renderTile(event.getTile())));
        eventBus.subscribe(BuildingEvents.BuildingActivatedEvent.class, event ->
                Platform.runLater(() -> renderTile(event.getTile())));
        eventBus.subscribe(BuildingEvents.BuildingDeactivatedEvent.class, event ->
                Platform.runLater(() -> renderTile(event.getTile())));
        eventBus.subscribe(BuildingEvents.BuildingConstructionProgressEvent.class, event ->
                Platform.runLater(() -> renderTile(event.getBuilding().getLocation())));
    }

    /**
     * Sets the hex grid to display.
     */
    public void setGrid(HexGrid grid) {
        this.grid = grid;
        if (grid != null) {
            totalHexagons = grid.getWidth() * grid.getHeight();
        }
        draw();
    }

    /**
     * Sets the debug overlay to report rendering statistics to.
     */
    public void setDebugOverlay(DebugOverlay debugOverlay) {
        this.debugOverlay = debugOverlay;
    }

    /**
     * Sets the selected tile and redraws the map.
     */
    public void setSelectedTile(Tile tile) {
        this.selectedTile = tile;
        draw();
    }

    /**
     * Handles the tile updated event.
     */
    private void handleTileUpdated(TileEvents.TileUpdatedEvent event) {
        Platform.runLater(() -> renderTile(event.getTile()));
    }

    /**
     * Handles the refresh map event.
     */
    private void handleRefreshMap(TileEvents.RefreshMapEvent event) {
        Platform.runLater(this::draw);
    }

    /**
     * Handles mouse press events.
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
     */
    private void handleMouseReleased(MouseEvent event) {
        isDragging = false;
    }

    /**
     * Handles mouse moved events for hover effect.
     */
    private void handleMouseMoved(MouseEvent event) {
        if (grid == null) return;

        double mouseX = event.getX();
        double mouseY = event.getY();

        Tile tile = getTileAtScreenPosition(mouseX, mouseY);

        if (tile != null && tile.isRevealed()) {
            if (hoveredTile != tile) {
                hoveredTile = tile;
                draw();
            }
        } else if (hoveredTile != null) {
            hoveredTile = null;
            draw();
        }
    }

    /**
     * Handles mouse click events for tile selection.
     */
    private void handleMouseClicked(MouseEvent event) {
        if (grid == null || isDragging) return;

        if (event.getButton() == MouseButton.PRIMARY) {
            double mouseX = event.getX();
            double mouseY = event.getY();

            Tile tile = getTileAtScreenPosition(mouseX, mouseY);

            if (tile != null && tile.isRevealed()) {
                selectedTile = tile;
                draw();

                eventBus.publish(new TileEvents.TileSelectedEvent(tile));
            }
        }
    }

    /**
     * Converts screen coordinates to grid coordinates and returns the tile.
     */
    private Tile getTileAtScreenPosition(double screenX, double screenY) {
        if (grid == null) return null;

        double worldX = (screenX - translateX) / scale;
        double worldY = (screenY - translateY) / scale;

        double hexHeight = hexSize * Math.sqrt(3);

        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                double centerX = x * hexSize * 1.5;
                double centerY = y * hexHeight;

                if (x % 2 == 1) {
                    centerY += hexHeight / 2;
                }

                double distance = Math.sqrt(Math.pow(worldX - centerX, 2) + Math.pow(worldY - centerY, 2));
                if (distance <= hexSize) {
                    return grid.getTileAt(x, y);
                }
            }
        }

        return null;
    }

    /**
     * Handles scroll events for zooming.
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
     * Draws the map view and records rendering statistics.
     */
    public void draw() {
        if (getWidth() <= 0 || getHeight() <= 0 || grid == null) return;

        if (debugOverlay != null) {
            debugOverlay.recordFrame();
        }

        long startTime = System.nanoTime();

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

        lastRenderTimeNs = System.nanoTime() - startTime;
        updateDebugOverlay();
    }

    /**
     * Updates the debug overlay with current rendering statistics.
     */
    private void updateDebugOverlay() {
        if (debugOverlay != null) {
            double renderTimeMs = lastRenderTimeNs / 1_000_000.0; // Convert ns to ms
            debugOverlay.setRenderStats(visibleHexagons, totalHexagons, renderTimeMs);
        }
    }

    /**
     * Draws the hexagonal grid.
     */
    private void drawHexGrid() {
        int width = grid.getWidth();
        int height = grid.getHeight();

        visibleHexagons = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile tile = grid.getTileAt(x, y);
                if (tile != null) {
                    drawHexagon(tile);
                    visibleHexagons++;
                }
            }
        }
    }

    /**
     * Renders a specific tile.
     */
    public void renderTile(Tile tile) {
        if (tile == null || getWidth() <= 0 || getHeight() <= 0 || grid == null) return;

        gc.save();
        gc.translate(translateX, translateY);
        gc.scale(scale, scale);

        drawHexagon(tile);

        gc.restore();
    }

    /**
     * Public method to re-render the entire grid.
     * Used by external components.
     */
    public void renderGrid() {
        draw();
    }

    /**
     * Draws a hexagon for the specified tile.
     */
    private void drawHexagon(Tile tile) {
        int gridX = tile.getX();
        int gridY = tile.getY();
        TerrainType terrainType = tile.getTerrainType();

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

        if (!tile.isRevealed()) {
            gc.setFill(Color.rgb(10, 10, 15, 0.9));
            gc.setStroke(Color.rgb(30, 30, 40));
            gc.setLineWidth(1);
        } else {
            gc.setFill(terrainType.getColor());

            boolean isSelected = (selectedTile != null &&
                    selectedTile.getX() == tile.getX() &&
                    selectedTile.getY() == tile.getY());

            boolean isHovered = (hoveredTile != null &&
                    hoveredTile.getX() == tile.getX() &&
                    hoveredTile.getY() == tile.getY());

            if (isSelected) {
                gc.setStroke(Color.WHITE);
                gc.setLineWidth(2);
            } else if (isHovered) {
                gc.setStroke(Color.YELLOW);
                gc.setLineWidth(1.5);
            } else if (tile.isColonized()) {
                gc.setStroke(Color.rgb(255, 255, 255, 0.7));
                gc.setLineWidth(1.5);
            } else {
                gc.setStroke(Color.rgb(80, 80, 100));
                gc.setLineWidth(1);
            }
        }

        gc.beginPath();
        gc.moveTo(xPoints[0], yPoints[0]);

        for (int i = 1; i < 6; i++) {
            gc.lineTo(xPoints[i], yPoints[i]);
        }

        gc.closePath();
        gc.fill();
        gc.stroke();

        if (tile.isRevealed()) {
            if (tile.isColonized()) {
                gc.setFill(Color.rgb(255, 255, 255, 0.8));  // Increased opacity
                gc.fillOval(centerX - hexSize/3.5, centerY - hexSize/3.5, hexSize/1.75, hexSize/1.75);  // Larger indicator

                if (tile.hasBuilding()) {
                    if (tile.getBuilding().isComplete()) {
                        if (tile.getBuilding().isActive()) {
                            gc.setFill(Color.rgb(50, 200, 50, 0.8));  // Green for active
                        } else {
                            gc.setFill(Color.rgb(200, 50, 50, 0.8));  // Red for inactive
                        }
                        gc.fillRect(centerX - hexSize/3, centerY - hexSize/3, hexSize/1.5, hexSize/1.5);
                    } else {
                        gc.setFill(Color.rgb(255, 165, 0, 0.8));  // Orange for construction
                        gc.fillRect(centerX - hexSize/3, centerY - hexSize/3, hexSize/1.5, hexSize/1.5);

                        double progress = tile.getBuilding().getConstructionProgress() / 100.0;
                        gc.setFill(Color.rgb(50, 200, 50, 0.6));  // Green for progress
                        gc.fillRect(centerX - hexSize/3, centerY - hexSize/3, hexSize/1.5 * progress, hexSize/1.5);
                    }
                }
            }

            if (scale > 1.5) {
                gc.setFill(Color.WHITE);
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText(gridX + "," + gridY, centerX, centerY);
            }
        }
    }

    /**
     * Resets the view to center the map and reset zoom.
     */
    public void resetView() {
        if (grid == null) return;

        int centerX = grid.getWidth() / 2;
        int centerY = grid.getHeight() / 2;

        double centerHexX = centerX * hexSize * 1.5;
        double centerHexY = centerY * hexSize * Math.sqrt(3);

        if (centerX % 2 == 1) {
            centerHexY += hexSize * Math.sqrt(3) / 2;
        }

        translateX = getWidth() / 2 - centerHexX * scale;
        translateY = getHeight() / 2 - centerHexY * scale;

        scale = 1.0;
        draw();
    }

    /**
     * Handles resizing of the view.
     */
    @Override
    public void resize(double width, double height) {
        super.resize(width, height);
        draw();
    }

    public void dispose() {
        LOGGER.fine("Disposing MapView resources");

        eventBus.unsubscribeAll(this);

        grid = null;
        selectedTile = null;
        hoveredTile = null;
        debugOverlay = null;
    }
}