package com.colonygenesis.ui.debug;

import com.colonygenesis.core.Game;
import com.colonygenesis.ui.events.BuildingEvents;
import com.colonygenesis.ui.events.ColonyEvents;
import com.colonygenesis.ui.events.EventBus;
import com.colonygenesis.ui.events.TurnEvents;
import com.colonygenesis.ui.styling.AppTheme;
import com.colonygenesis.util.LoggerUtil;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Debug overlay for displaying real-time performance metrics during gameplay.
 * Includes FPS counter, memory usage, rendering statistics, and game state info.
 */
public class DebugOverlay extends VBox {
    private static final Logger LOGGER = LoggerUtil.getLogger(DebugOverlay.class);

    private static final double UPDATE_INTERVAL_MS = 500; // Update every 500ms
    private static final DecimalFormat df = new DecimalFormat("#.##");
    private static final DecimalFormat memoryFormat = new DecimalFormat("#,###");

    private final Game game;
    private final Map<DebugSection, VBox> sections = new HashMap<>();
    private final Timeline updateTimeline;
    private final EventBus eventBus;

    // FPS tracking
    private long lastFrameTime = System.nanoTime();
    private int frameCount = 0;
    private double fps = 0;

    // Performance metrics
    private final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    private final Runtime runtime = Runtime.getRuntime();

    private final Label fpsLabel = new Label("FPS: 0");
    private final Label heapMemoryLabel = new Label("Heap: 0 MB / 0 MB");
    private final Label nonHeapMemoryLabel = new Label("Non-Heap: 0 MB");
    private final Label gcLabel = new Label("GC Count: 0");
    private final Label renderTimeLabel = new Label("Render: 0 ms");
    private final Label renderStatsLabel = new Label("Visible Hexes: 0/0");
    private final Label gameStateLabel = new Label("Turn: 0, Phase: -");
    private final Label entityCountLabel = new Label("Entities: 0");

    // Settings
    private boolean active = false;
    private boolean expanded = true;

    /**
     * Creates a new debug overlay for the specified game.
     *
     * @param game The game to monitor
     */
    public DebugOverlay(Game game) {
        this.game = game;
        this.eventBus = EventBus.getInstance();

        getStyleClass().add("debug-overlay");
        setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); -fx-background-radius: 5;");
        setPadding(new Insets(5));
        setSpacing(2);
        setMaxWidth(250);
        setMaxHeight(600);
        setAlignment(Pos.TOP_LEFT);

        createPerformanceSection();
        createRenderingSection();
        createGameStateSection();

        createHeader();

        updateTimeline = new Timeline(new KeyFrame(Duration.millis(UPDATE_INTERVAL_MS), e -> updateMetrics()));
        updateTimeline.setCycleCount(Animation.INDEFINITE);

        initializeEventSubscriptions();

        LOGGER.fine("Debug overlay initialized");
    }

    /**
     * Initialize subscriptions to relevant events.
     */
    private void initializeEventSubscriptions() {
        eventBus.subscribe(TurnEvents.TurnAdvancedEvent.class, event ->
                Platform.runLater(this::updateGameStateMetrics));
        eventBus.subscribe(TurnEvents.PhaseChangedEvent.class, event ->
                Platform.runLater(this::updateGameStateMetrics));

        eventBus.subscribe(BuildingEvents.BuildingPlacedEvent.class, event ->
                Platform.runLater(this::updateBuildingMetrics));
        eventBus.subscribe(BuildingEvents.BuildingCompletedEvent.class, event ->
                Platform.runLater(this::updateBuildingMetrics));

        eventBus.subscribe(ColonyEvents.PopulationChangedEvent.class, event ->
                Platform.runLater(this::updateGameStateMetrics));
    }

    /**
     * Creates the header section with title and controls.
     */
    private void createHeader() {
        HBox header = new HBox(5);
        header.setAlignment(Pos.CENTER_LEFT);

        Rectangle indicator = new Rectangle(10, 10, Color.LIMEGREEN);

        Label titleLabel = new Label("Debug Info");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");

        ToggleButton expandButton = new ToggleButton("▼");
        expandButton.setSelected(true);
        expandButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        expandButton.setOnAction(e -> {
            expanded = expandButton.isSelected();
            expandButton.setText(expanded ? "▼" : "▶");
            for (VBox section : sections.values()) {
                section.setVisible(expanded);
                section.setManaged(expanded);
            }
        });

        HBox spacer = new HBox();
        spacer.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        header.getChildren().addAll(indicator, titleLabel, spacer, expandButton);
        getChildren().add(header);
    }

    /**
     * Creates the performance metrics section (FPS, memory).
     */
    private void createPerformanceSection() {
        VBox section = createSection("Performance", DebugSection.PERFORMANCE);

        fpsLabel.setStyle("-fx-text-fill: white;");
        heapMemoryLabel.setStyle("-fx-text-fill: white;");
        nonHeapMemoryLabel.setStyle("-fx-text-fill: white;");
        gcLabel.setStyle("-fx-text-fill: white;");

        section.getChildren().addAll(fpsLabel, heapMemoryLabel, nonHeapMemoryLabel, gcLabel);
    }

    /**
     * Creates the rendering statistics section.
     */
    private void createRenderingSection() {
        VBox section = createSection("Rendering", DebugSection.RENDERING);

        renderTimeLabel.setStyle("-fx-text-fill: white;");
        renderStatsLabel.setStyle("-fx-text-fill: white;");

        section.getChildren().addAll(renderTimeLabel, renderStatsLabel);
    }

    /**
     * Creates the game state section.
     */
    private void createGameStateSection() {
        VBox section = createSection("Game State", DebugSection.GAME_STATE);

        gameStateLabel.setStyle("-fx-text-fill: white;");
        entityCountLabel.setStyle("-fx-text-fill: white;");

        section.getChildren().addAll(gameStateLabel, entityCountLabel);
    }

    /**
     * Helper method to create a section with a title.
     *
     * @param title The section title
     * @param section The section type
     * @return The section container
     */
    private VBox createSection(String title, DebugSection section) {
        VBox container = new VBox(2);
        container.setPadding(new Insets(5, 0, 5, 0));

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");

        container.getChildren().add(titleLabel);

        sections.put(section, container);
        getChildren().add(container);

        return container;
    }

    /**
     * Updates all metrics displayed in the overlay.
     */
    private void updateMetrics() {
        if (!active) return;

        updatePerformanceMetrics();
        updateRenderingMetrics();
        updateGameStateMetrics();
    }

    /**
     * Updates performance-related metrics (FPS, memory).
     */
    private void updatePerformanceMetrics() {
        long currentTime = System.nanoTime();
        double frameTime = (currentTime - lastFrameTime) / 1_000_000_000.0;
        frameCount++;

        if (frameTime > 1.0) {
            fps = frameCount / frameTime;
            frameCount = 0;
            lastFrameTime = currentTime;
        }

        MemoryUsage heapMemory = memoryMXBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemory = memoryMXBean.getNonHeapMemoryUsage();

        long usedHeapMB = heapMemory.getUsed() / (1024 * 1024);
        long maxHeapMB = heapMemory.getMax() / (1024 * 1024);
        long usedNonHeapMB = nonHeapMemory.getUsed() / (1024 * 1024);

        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        Platform.runLater(() -> {
            fpsLabel.setText("FPS: " + df.format(fps));

            Color fpsColor = fps > 45 ? Color.LIMEGREEN : (fps > 30 ? Color.YELLOW : Color.RED);
            fpsLabel.setTextFill(fpsColor);

            double heapUsagePercent = (double) usedHeapMB / maxHeapMB * 100;
            String heapPercentStr = df.format(heapUsagePercent) + "%";

            Color memoryColor = heapUsagePercent < 70 ? Color.LIMEGREEN :
                    (heapUsagePercent < 85 ? Color.YELLOW : Color.RED);

            heapMemoryLabel.setText("Heap: " + memoryFormat.format(usedHeapMB) + " MB / "
                    + memoryFormat.format(maxHeapMB) + " MB (" + heapPercentStr + ")");
            heapMemoryLabel.setTextFill(memoryColor);

            nonHeapMemoryLabel.setText("Non-Heap: " + memoryFormat.format(usedNonHeapMB) + " MB");

            long usedMemoryMB = usedMemory / (1024 * 1024);
            gcLabel.setText("Used Memory: " + memoryFormat.format(usedMemoryMB) + " MB");
        });
    }

    /**
     * Updates rendering-related metrics.
     */
    private void updateRenderingMetrics() {
        int visibleHexes = 0;
        int totalHexes = 0;
        double renderTimeMs = 0;
    }

    /**
     * Updates game state metrics.
     */
    private void updateGameStateMetrics() {
        if (game != null && game.isInitialized()) {
            int turn = game.getCurrentTurn();
            String phase = game.getTurnManager().getCurrentPhase().getName();
            int entityCount = game.getBuildingManager().getBuildingCount();

            Platform.runLater(() -> {
                gameStateLabel.setText("Turn: " + turn + ", Phase: " + phase);
                entityCountLabel.setText("Buildings: " + entityCount);
            });
        }
    }

    /**
     * Updates building-related metrics.
     */
    private void updateBuildingMetrics() {
        if (game != null && game.isInitialized()) {
            int buildingCount = game.getBuildingManager().getBuildingCount();
            int constructionCount = game.getBuildingManager().getBuildingsUnderConstructionCount();
            int activeCount = game.getBuildingManager().getActiveBuildings();

            Platform.runLater(() -> {
                entityCountLabel.setText("Buildings: " + buildingCount +
                        " (Active: " + activeCount +
                        ", Building: " + constructionCount + ")");
            });
        }
    }

    /**
     * Sets rendering statistics from the map view.
     *
     * @param visibleHexes Number of visible hexagons
     * @param totalHexes Total number of hexagons
     * @param renderTimeMs Render time in milliseconds
     */
    public void setRenderStats(int visibleHexes, int totalHexes, double renderTimeMs) {
        Platform.runLater(() -> {
            renderTimeLabel.setText("Render Time: " + df.format(renderTimeMs) + " ms");

            if (totalHexes > 0) {
                double percentage = (double) visibleHexes / totalHexes * 100;
                renderStatsLabel.setText("Visible Hexes: " + visibleHexes + "/" + totalHexes
                        + " (" + df.format(percentage) + "%)");
            } else {
                renderStatsLabel.setText("Visible Hexes: " + visibleHexes + "/" + totalHexes);
            }
        });
    }

    /**
     * Sets entity counts for debug display.
     *
     * @param buildingCount Number of buildings
     * @param otherEntities Number of other entities
     */
    public void setEntityCounts(int buildingCount, int otherEntities) {
        Platform.runLater(() -> {
            entityCountLabel.setText("Buildings: " + buildingCount + ", Other: " + otherEntities);
        });
    }

    /**
     * Records a rendered frame for FPS calculation.
     */
    public void recordFrame() {
        frameCount++;
    }

    /**
     * Shows or hides the debug overlay.
     *
     * @param active Whether the overlay should be active
     */
    public void setActive(boolean active) {
        this.active = active;
        setVisible(active);
        setManaged(active);

        if (active && !updateTimeline.getStatus().equals(Animation.Status.RUNNING)) {
            updateTimeline.play();
            LOGGER.fine("Debug overlay activated");
        } else if (!active && updateTimeline.getStatus().equals(Animation.Status.RUNNING)) {
            updateTimeline.stop();
            LOGGER.fine("Debug overlay deactivated");
        }
    }

    /**
     * Toggles the visibility of the debug overlay.
     *
     * @return The new active state
     */
    public boolean toggleActive() {
        setActive(!active);
        return active;
    }

    /**
     * Toggles the visibility of a specific debug section.
     *
     * @param section The section to toggle
     * @return Whether the section is now visible
     */
    public boolean toggleSection(DebugSection section) {
        VBox sectionBox = sections.get(section);
        if (sectionBox != null) {
            boolean visible = !sectionBox.isVisible();
            sectionBox.setVisible(visible);
            sectionBox.setManaged(visible);
            return visible;
        }
        return false;
    }

    /**
     * Cleans up resources when the overlay is no longer needed.
     */
    public void dispose() {
        updateTimeline.stop();
        LOGGER.fine("Debug overlay disposed");
    }

    /**
     * Enumeration of debug overlay sections.
     */
    public enum DebugSection {
        PERFORMANCE,
        RENDERING,
        GAME_STATE
    }
}