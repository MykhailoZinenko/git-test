package com.colonygenesis.ui.components;

import com.colonygenesis.technology.TechBranch;
import com.colonygenesis.technology.TechManager;
import com.colonygenesis.technology.Technology;
import com.colonygenesis.technology.TechnologyTree;
import com.colonygenesis.ui.events.EventBus;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Component for displaying the research tree graphically.
 */
public class ResearchTreeView extends Pane {
    private final TechManager techManager;
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final Map<Technology, TechNode> nodes;
    private final List<TechConnector> connectors;

    private double translateX = 0;
    private double translateY = 0;  // Added vertical translation
    private double scale = 1.0;
    private boolean isDragging = false;
    private double lastMouseX, lastMouseY;
    private TechNode selectedNode;

    private static final double NODE_RADIUS = 30;
    private static final double HORIZONTAL_SPACING = 200;
    private static final double VERTICAL_SPACING = 150;  // Space between branches
    private static final double SAME_TIER_SPACING = 100;  // Space between nodes in same tier

    public ResearchTreeView(TechManager techManager) {
        this.techManager = techManager;
        this.nodes = new HashMap<>();
        this.connectors = new ArrayList<>();

        canvas = new Canvas();
        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());
        getChildren().add(canvas);

        gc = canvas.getGraphicsContext2D();

        setupNodes();
        setupConnectors();
        setupEventHandlers();

        draw();
    }

    private void setupNodes() {
        TechnologyTree tree = techManager.getTechTree();

        // Group technologies by branch and tier for better positioning
        Map<TechBranch, Map<Integer, List<Technology>>> branchTierMap = new HashMap<>();

        // Initialize the map
        for (TechBranch branch : TechBranch.values()) {
            branchTierMap.put(branch, new HashMap<>());
        }

        // Group technologies
        for (Technology tech : tree.getAllTechnologies()) {
            TechBranch branch = tech.getBranch();
            int tier = tech.getTier();

            branchTierMap.get(branch)
                    .computeIfAbsent(tier, k -> new ArrayList<>())
                    .add(tech);
        }

        // Position nodes with proper spacing
        double currentY = 100;  // Start position

        for (TechBranch branch : TechBranch.values()) {
            Map<Integer, List<Technology>> tierMap = branchTierMap.get(branch);
            double maxYForBranch = currentY;  // Track the maximum Y position for this branch

            for (Map.Entry<Integer, List<Technology>> tierEntry : tierMap.entrySet()) {
                int tier = tierEntry.getKey();
                List<Technology> techs = tierEntry.getValue();

                // Calculate x position based on tier
                double x = 100 + (tier - 1) * HORIZONTAL_SPACING;

                // Calculate y positions for techs in this tier
                for (int i = 0; i < techs.size(); i++) {
                    Technology tech = techs.get(i);
                    double y = currentY;

                    // If multiple techs in same tier, distribute them vertically
                    if (techs.size() > 1) {
                        y += i * SAME_TIER_SPACING;
                    }

                    TechNode node = new TechNode(tech, x, y);
                    nodes.put(tech, node);

                    // Update maxY for this branch
                    maxYForBranch = Math.max(maxYForBranch, y);
                }
            }

            // Move to next branch position (bottom of current branch + spacing)
            currentY = maxYForBranch + VERTICAL_SPACING;
        }
    }

    private void setupConnectors() {
        for (Technology tech : techManager.getTechTree().getAllTechnologies()) {
            TechNode toNode = nodes.get(tech);

            for (String prereqId : tech.getTechPrerequisites()) {
                Technology prereq = techManager.getTechTree().getTechnology(prereqId);
                if (prereq != null) {
                    TechNode fromNode = nodes.get(prereq);
                    if (fromNode != null) {
                        connectors.add(new TechConnector(fromNode, toNode));
                    }
                }
            }
        }
    }

    private void setupEventHandlers() {
        setOnMousePressed(this::handleMousePressed);
        setOnMouseDragged(this::handleMouseDragged);
        setOnMouseReleased(this::handleMouseReleased);
        setOnMouseClicked(this::handleMouseClicked);
        setOnScroll(this::handleScroll);

        widthProperty().addListener((obs, oldVal, newVal) -> draw());
        heightProperty().addListener((obs, oldVal, newVal) -> draw());
    }

    private void handleMousePressed(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            lastMouseX = event.getX();
            lastMouseY = event.getY();
            isDragging = true;
        }
    }

    private void handleMouseDragged(MouseEvent event) {
        if (isDragging) {
            double dx = event.getX() - lastMouseX;
            double dy = event.getY() - lastMouseY;
            translateX += dx;
            translateY += dy;  // Added vertical dragging
            lastMouseX = event.getX();
            lastMouseY = event.getY();
            draw();
        }
    }

    private void handleMouseReleased(MouseEvent event) {
        isDragging = false;
    }

    private void handleMouseClicked(MouseEvent event) {
        double mouseX = (event.getX() - translateX) / scale;
        double mouseY = (event.getY() - translateY) / scale;  // Updated to account for Y translation

        for (TechNode node : nodes.values()) {
            if (node.contains(mouseX, mouseY)) {
                selectNode(node);
                EventBus.getInstance().publish(new TechSelectedEvent(node.getTechnology()));
                break;
            }
        }
    }

    private void handleScroll(ScrollEvent event) {
        if (event.isControlDown()) {
            // Zoom
            double zoomFactor = 1.05;
            if (event.getDeltaY() < 0) {
                zoomFactor = 1 / zoomFactor;
            }

            double newScale = scale * zoomFactor;
            if (newScale >= 0.5 && newScale <= 2.0) {
                scale = newScale;
                draw();
            }
        } else {
            // Panning (horizontal and vertical)
            translateX += event.getDeltaX();
            translateY += event.getDeltaY();
            draw();
        }
    }

    private void selectNode(TechNode node) {
        if (selectedNode != null) {
            selectedNode.setSelected(false);
        }
        selectedNode = node;
        if (selectedNode != null) {
            selectedNode.setSelected(true);
        }
        draw();
    }

    private void draw() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.save();
        gc.translate(translateX, translateY);  // Apply both X and Y translation
        gc.scale(scale, scale);

        // Draw connectors
        for (TechConnector connector : connectors) {
            connector.draw(gc);
        }

        // Draw nodes
        for (TechNode node : nodes.values()) {
            node.updateState(techManager);
            node.draw(gc);
        }

        gc.restore();
    }

    public void refresh() {
        draw();
    }

    // Inner class for tech nodes
    private static class TechNode {
        private final Technology tech;
        private double x, y;
        private NodeState state;
        private boolean selected;

        public TechNode(Technology tech, double x, double y) {
            this.tech = tech;
            this.x = x;
            this.y = y;
            this.state = NodeState.LOCKED;
            this.selected = false;
        }

        public void updateState(TechManager manager) {
            if (manager.isTechResearched(tech.getId())) {
                state = NodeState.RESEARCHED;
            } else if (manager.canResearch(tech)) {
                state = NodeState.AVAILABLE;
            } else {
                state = NodeState.LOCKED;
            }
        }

        public void draw(GraphicsContext gc) {
            // Draw node background
            gc.setFill(state.getColor());
            gc.fillOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);

            // Draw branch color outline
            gc.setStroke(tech.getBranch().getColor());
            gc.setLineWidth(3);
            gc.strokeOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);

            // Draw selection highlight
            if (selected) {
                gc.setStroke(Color.WHITE);
                gc.setLineWidth(4);
                gc.strokeOval(x - NODE_RADIUS - 3, y - NODE_RADIUS - 3,
                        (NODE_RADIUS + 3) * 2, (NODE_RADIUS + 3) * 2);
            }

            // Draw tech name - position below the node with proper alignment
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("System", FontWeight.BOLD, 14));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);

            // Draw the name centered below the node
            String name = tech.getName();
            gc.fillText(name, x, y + NODE_RADIUS + 25);

            // Draw tier number inside the node
            gc.setFont(Font.font("System", FontWeight.BOLD, 18));
            gc.setFill(Color.WHITE);
            gc.fillText(String.valueOf(tech.getTier()), x, y);
        }

        public boolean contains(double mouseX, double mouseY) {
            double dx = mouseX - x;
            double dy = mouseY - y;
            return dx * dx + dy * dy <= NODE_RADIUS * NODE_RADIUS;
        }

        public Technology getTechnology() { return tech; }
        public void setSelected(boolean selected) { this.selected = selected; }
    }

    // Inner class for tech nodes
    private enum NodeState {
        RESEARCHED(Color.rgb(0, 200, 0)),    // Green
        AVAILABLE(Color.rgb(255, 215, 0)),   // Gold
        LOCKED(Color.rgb(100, 100, 100));    // Gray

        private final Color color;

        NodeState(Color color) {
            this.color = color;
        }

        public Color getColor() { return color; }
    }

    // Inner class for connections between nodes
    private static class TechConnector {
        private final TechNode from;
        private final TechNode to;

        public TechConnector(TechNode from, TechNode to) {
            this.from = from;
            this.to = to;
        }

        public void draw(GraphicsContext gc) {
            gc.setStroke(Color.rgb(75, 115, 153));
            gc.setLineWidth(2);
            gc.strokeLine(from.x, from.y, to.x, to.y);
        }
    }

    // Event for tech selection
    public static class TechSelectedEvent implements com.colonygenesis.ui.events.GameEvent {
        private final Technology technology;

        public TechSelectedEvent(Technology technology) {
            this.technology = technology;
        }

        public Technology getTechnology() { return technology; }

        @Override
        public String getName() { return "TechSelected"; }
    }
}