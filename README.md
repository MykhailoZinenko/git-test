[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/Q-troXqB)

# Exoplanet: Colony Genesis - Technical Documentation

## Project Overview
Exoplanet: Colony Genesis is a turn-based colony simulation game built with Java and JavaFX. The application implements a hexagonal tile-based environment where players establish and manage colonies on procedurally generated planets.

## Architecture
The project follows a modular architecture with clear separation of concerns:

- **Core Game Logic**: Manages game state, turn processing, and high-level game flow
- **Resource Management**: Handles resource production, consumption, and storage
- **Map System**: Implements hexagonal grid and terrain generation
- **Building System**: Manages construction and operation of different building types
- **UI System**: Provides the user interface and rendering using JavaFX

## Technology Stack
- **Language**: Java 21
- **UI Framework**: JavaFX 21
- **Build System**: Maven
- **Dependency Management**: Maven
- **Additional Libraries**:
  - ControlsFX (enhanced UI controls)
  - BootstrapFX (CSS styling)

## Project Structure

```
src/main/java/
├── com/colonygenesis/
│   ├── building/       # Building system implementation
│   ├── core/           # Core game mechanics and state
│   ├── map/            # Map and terrain generation
│   ├── resource/       # Resource management
│   ├── ui/             # User interface components
│   │   ├── components/ # Reusable UI components
│   │   ├── events/     # Event classes
│   │   ├── styling/    # UI styling constants
│   │   └── debug/      # Debug overlay
│   └── util/           # Utility classes
```

## Core Systems

### Turn-Based Game System
The game implements a turn-based system with distinct phases:

```java
public enum TurnPhase implements Serializable {
    PLANNING("Planning", "Plan your next actions", true),
    BUILDING("Building", "Construct and upgrade buildings", false),
    PRODUCTION("Production", "Resource production and consumption", false),
    EVENTS("Events", "Handle planetary events", false),
    END_TURN("End Turn", "Finalize turn and advance", false);
    
    private final String name;
    private final String description;
    private final boolean requiresInput;
}
```

Turn management is handled by the `TurnManager` class:

```java
public Result<TurnPhase> advancePhase() {
    if (currentPhase.requiresInput() && !phaseCompleted) {
        return Result.failure("Phase requires completion before advancing");
    }

    int ordinal = currentPhase.ordinal();
    TurnPhase previousPhase = currentPhase;

    TurnPhase[] phases = TurnPhase.values();
    currentPhase = phases[(ordinal + 1) % phases.length];
    phaseCompleted = false;

    eventBus.publish(new TurnEvents.PhaseChangedEvent(currentPhase, previousPhase, turnNumber));

    if (currentPhase == TurnPhase.END_TURN) {
        advanceTurn();
    } else if (!currentPhase.requiresInput()) {
        executeCurrentPhase();
    }

    return Result.success(currentPhase);
}
```

### Event System
The event system is a critical component that enables loose coupling between game subsystems:

#### EventBus Implementation
The `EventBus` is implemented as a thread-safe publish-subscribe system:

```java
public class EventBus {
    private static EventBus instance;
    private final Map<Class<? extends GameEvent>, List<EventHandler<? extends GameEvent>>> subscribers;

    private EventBus() {
        subscribers = new ConcurrentHashMap<>();
    }

    public static synchronized EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }
    
    public <T extends GameEvent> void subscribe(Class<T> eventType, EventHandler<T> handler) {
        subscribers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(handler);
    }

    @SuppressWarnings("unchecked")
    public <T extends GameEvent> void publish(T event) {
        if (subscribers.containsKey(event.getClass())) {
            for (EventHandler handler : subscribers.get(event.getClass())) {
                handler.handle(event);
            }
        }
    }
}
```

#### Event Hierarchy
Events follow an interface-based hierarchy:
- `GameEvent`: Base interface for all events
  - `TileEvents`: Events related to map interactions
  - `ResourceEvents`: Events related to resource changes
  - `BuildingEvents`: Events related to building operations
  - `TurnEvents`: Events related to turn progression
  - `NotificationEvents`: UI notification events

#### Event Processing
Events are processed asynchronously with Platform.runLater() for UI updates:

```java
eventBus.subscribe(TileEvents.TileSelectedEvent.class, event -> 
    Platform.runLater(() -> updateTileInfo(event.getTile())));
```

### Resource Management System
Resources are managed through the `ResourceManager` class, which handles resource operations with fail-safe transaction semantics:

```java
// Resource transaction system using Result pattern
public Result<Integer> removeResource(ResourceType type, int amount) {
    if (type == null) {
        return Result.failure("Resource type cannot be null");
    }

    int current = resources.getOrDefault(type, 0);

    if (current < amount) {
        return Result.failure("Not enough " + type.getName() + ": " + current + "/" + amount);
    }

    resources.put(type, current - amount);
    eventBus.publish(new ResourceEvents.ResourceChangedEvent(type, current - amount, current));
    return Result.success(amount);
}
```

#### Resource Architecture
- `ResourceType`: Enumeration of resource types with properties
- `ResourceManager`: Central manager for all resource operations
- `Resource events`: Notification system for UI updates
- `Production/consumption tracking`: Per-turn accounting system
- `Resource balance`: Automatic processing of shortages and surpluses

#### Population and Worker Management
Special handling for population as a resource with additional worker assignment tracking:

```java
public int assignWorkers(int workersToAssign) {
    int availableWorkers = getAvailableWorkers();
    int actualAssigned = Math.min(workersToAssign, availableWorkers);
    
    assignedWorkers += actualAssigned;
    
    // Publish worker availability event
    eventBus.publish(new ResourceEvents.WorkerAvailabilityChangedEvent(
            getAvailableWorkers(),
            availableWorkers,
            actualAssigned
    ));
    
    return actualAssigned;
}
```

### Map System

#### Hexagonal Grid Implementation
The game uses an offset coordinate system for hexagonal tiles with optimized neighbor calculation:

```java
public List<Tile> getNeighbors(Tile tile) {
    List<Tile> neighbors = new ArrayList<>();
    int x = tile.getX();
    int y = tile.getY();

    boolean isOddColumn = (x % 2 == 1);
    
    // Different neighbor coordinate offsets depending on column parity
    int[][] directions = isOddColumn ? 
        new int[][] {{0,-1}, {1,0}, {1,1}, {0,1}, {-1,1}, {-1,0}} : 
        new int[][] {{0,-1}, {1,-1}, {1,0}, {0,1}, {-1,0}, {-1,-1}};
    
    for (int[] dir : directions) {
        int nx = x + dir[0];
        int ny = y + dir[1];
        
        Tile neighbor = getTileAt(nx, ny);
        if (neighbor != null) {
            neighbors.add(neighbor);
        }
    }
    
    return neighbors;
}
```

#### Terrain Generation
The map generation uses a combination of:
- Simplex noise for terrain elevation
- Distribution templates based on planet type
- Probability-based terrain distribution
- Guaranteed habitable starting area

```java
public HexGrid generateMap(int width, int height, PlanetType planetType, long seed) {
    Random random = new Random(seed);
    HexGrid grid = new HexGrid(width, height);
    
    // Fill grid with default tiles
    for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
            Tile tile = new Tile(x, y, TerrainType.PLAINS);
            grid.setTileAt(x, y, tile);
        }
    }
    
    // Generate terrain using simplex noise and distribution templates
    PlanetType.TerrainDistribution distribution = planetType.getTerrainDistribution();
    SimplexNoise elevationNoise = new SimplexNoise(random.nextLong());
    
    for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
            double noise = elevationNoise.noise(x * 0.1, y * 0.1);
            
            TerrainType terrainType;
            if (noise > 0.7) {
                terrainType = TerrainType.MOUNTAINS;
            } else if (noise < -0.7) {
                terrainType = TerrainType.WATER;
            } else {
                terrainType = distribution.getRandomTerrain(random);
            }
            
            grid.getTileAt(x, y).setTerrainType(terrainType);
        }
    }
    
    ensureHabitableCenter(grid, width, height);
    return grid;
}
```

#### Colonization System
The `ColonizationManager` handles the colonization of new tiles with prerequisites:
- Must be adjacent to an existing colonized tile
- Must be habitable terrain
- Requires specified resources for colonization
- Automatically reveals neighboring tiles upon colonization

### Building System

#### Building Architecture
Buildings follow a robust inheritance hierarchy to maximize code reuse:

```
AbstractBuilding (abstract)
├── ProductionBuilding (abstract)
│   ├── ResourceProducer
│   └── AdvancedProducer
└── HabitationBuilding (abstract)
    ├── BasicHousing
    └── AdvancedHousing
```

The `AbstractBuilding` class defines the core functionality:

```java
public abstract class AbstractBuilding implements Serializable {
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
    protected transient ResourceManager resourceManager;
    
    // Construction progress, worker assignment, and activation methods...
    
    // Abstract method for building operation
    public abstract Map<ResourceType, Integer> operate();
}
```

#### Construction System
The building system implements a multi-turn construction process:

```java
public boolean progressConstruction() {
    if (isComplete()) {
        return true;
    }

    int previousProgress = getConstructionProgress();
    remainingConstructionTime--;
    int newProgress = getConstructionProgress();

    eventBus.publish(new BuildingEvents.BuildingConstructionProgressEvent(
        this, previousProgress, newProgress));

    if (remainingConstructionTime <= 0) {
        eventBus.publish(new BuildingEvents.BuildingCompletedEvent(this));
        return true;
    }

    return false;
}
```

#### Worker Assignment
Buildings implement a worker assignment system with efficiency scaling:

```java
public int calculateEfficiency() {
    if (workersRequired == 0) return 100; // No workers needed = 100% efficiency
    return Math.min(100, (workersAssigned * 100) / workersRequired);
}
```

#### Building Management
The `BuildingManager` handles construction, demolition, and operation of all buildings:

```java
public Result<AbstractBuilding> constructBuilding(AbstractBuilding building) {
    // Validation and resource cost checks
    
    // Resource deduction
    for (Map.Entry<ResourceType, Integer> entry : constructionCost.entrySet()) {
        ResourceType type = entry.getKey();
        int amount = entry.getValue();
        resourceManager.removeResource(type, amount);
    }
    
    // Building registration
    buildings.add(building);
    buildingsByTile.put(location, building);
    location.setBuilding(building);
    
    // Construction queue management
    if (building.getRemainingConstructionTime() > 0) {
        buildingsUnderConstruction.add(building);
    } else {
        if (building.getWorkersRequired() == 0) {
            building.activate();
        }
    }
    
    // Event publication
    eventBus.publish(new BuildingEvents.BuildingPlacedEvent(building, location));
    
    return Result.success(building);
}
```

### UI Architecture

#### Screen Management
The `ScreenManager` implements a singleton pattern to handle screen transitions with lifecycle management:

```java
public class ScreenManager {
    private static ScreenManager instance;
    private Stage primaryStage;
    private IScreenController currentScreen;
    private final Map<GameState, IScreenController> screens = new HashMap<>();
    private final StackPane rootPane = new StackPane();
    
    public static ScreenManager getInstance() {
        if (instance == null) {
            instance = new ScreenManager();
        }
        return instance;
    }
    
    public void activateScreen(GameState gameState) {
        IScreenController newScreen = screens.get(gameState);
        
        if (currentScreen != null) {
            currentScreen.onHide();
        }
        
        currentScreen = newScreen;
        rootPane.getChildren().clear();
        rootPane.getChildren().add(currentScreen.getRoot());
        currentScreen.onShow();
    }
}
```

#### Screen Controller Interface
Screen controllers follow a common interface with lifecycle methods:

```java
public interface IScreenController {
    Parent getRoot();
    void initialize();
    void onShow();
    void onHide();
    void update();
}
```

#### Map Rendering System
The map view implements efficient rendering of the hexagonal grid with culling and caching:

```java
public void draw() {
    gc.save();
    gc.translate(translateX, translateY);
    gc.scale(scale, scale);

    // Only render visible tiles
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
            Tile tile = grid.getTileAt(x, y);
            if (tile != null) {
                // Calculate screen position
                double centerX = x * hexSize * 1.5;
                double centerY = y * hexSize * Math.sqrt(3);
                if (x % 2 == 1) {
                    centerY += hexSize * Math.sqrt(3) / 2;
                }
                
                // Check if tile is in view
                if (isInView(centerX, centerY, hexSize)) {
                    drawHexagon(tile, centerX, centerY);
                    visibleHexagons++;
                }
            }
        }
    }
    
    gc.restore();
}
```

#### Component System
The UI uses a component-based design with reusable elements:
- `GamePanel`: Base panel component for consistent styling
- `ResourceDisplay`: Component for showing resource information
- `ResourceBar`: Container for resource displays
- `ActionButton`: Custom button with standardized styling
- `TileInfoPanel`: Display panel for selected tile information
- `BuildingSelectionOverlay`: Modal dialog for building selection

### Game State Management

#### Serialization System
The game implements comprehensive serialization with proper transient field handling:

```java
public String saveGame() {
    // Validate game state before saving
    if (validateGameState()) {
        return null;
    }

    // Create save directory if needed
    Path savesDir = Paths.get("saves");
    if (!Files.exists(savesDir)) {
        Files.createDirectories(savesDir);
    }

    // Generate timestamped filename
    this.saveDate = LocalDateTime.now();
    String timestamp = saveDate.format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
    String filename = "saves/" + timestamp + "_" + sanitizeFilename(colonyName) + "_Turn" + currentTurn + ".save";

    // Write to file using Java serialization
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
        oos.writeObject(this);
        return filename;
    }
}
```

#### Transient Field Handling
Special handling for transient fields during deserialization:

```java
@Serial
private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
    in.defaultReadObject();
    
    // Reinitialize transient fields
    this.eventBus = EventBus.getInstance();
    
    // Reconnect object references
    if (turnManager != null) {
        turnManager.setGame(this);
    }
    
    if (buildingManager != null) {
        buildingManager.setGame(this);
        buildingManager.updateResourceManagerReferences();
    }
}
```

#### Save Game Management
The system provides functionality to list and load saved games:

```java
public static List<SaveGameInfo> getSavedGames() {
    List<SaveGameInfo> savedGames = new ArrayList<>();
    Path savesDir = Paths.get("saves");
    
    if (Files.exists(savesDir)) {
        Files.list(savesDir)
            .filter(path -> path.toString().endsWith(".save"))
            .forEach(path -> {
                try {
                    Game game = loadGameInfo(path.toString());
                    if (game != null) {
                        SaveGameInfo saveInfo = new SaveGameInfo(
                            path.toString(),
                            game.getColonyName(),
                            game.getPlanetType(),
                            game.getCurrentTurn(),
                            game.getSaveDate(),
                            game.getMapSize()
                        );
                        savedGames.add(saveInfo);
                    }
                } catch (Exception e) {
                    // Log error and continue
                }
            });
    }
    
    return savedGames;
}
```

## Design Patterns

### Singleton Pattern
Used for services that need global access with a single instance:
```java
public static synchronized EventBus getInstance() {
    if (instance == null) {
        instance = new EventBus();
    }
    return instance;
}
```

### Factory Pattern
Implemented for creating different types of buildings:
```java
public class BuildingOptionButton extends HBox {
    private final AbstractBuilding building;

    public BuildingOptionButton(String name, String description, BuildingSupplier supplier) {
        this.building = supplier.get();
        // UI initialization...
    }
}

@FunctionalInterface
private interface BuildingSupplier {
    AbstractBuilding get();
}
```

### Observer Pattern
The EventBus implements the observer pattern for loosely coupled event handling:
```java
public <T extends GameEvent> void subscribe(Class<T> eventType, EventHandler<T> handler) {
    subscribers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(handler);
}

@SuppressWarnings("unchecked")
public <T extends GameEvent> void publish(T event) {
    if (subscribers.containsKey(event.getClass())) {
        for (EventHandler handler : subscribers.get(event.getClass())) {
            handler.handle(event);
        }
    }
}
```

### Strategy Pattern
Used for different building behaviors through polymorphism:
```java
// AbstractBuilding defines the strategy interface
public abstract Map<ResourceType, Integer> operate();

// Different implementations provide different strategies
@Override
public Map<ResourceType, Integer> operate() {
    Map<ResourceType, Integer> output = super.operate();

    if (isActive()) {
        for (int i = 0; i < inputTypes.size(); i++) {
            ResourceType inputType = inputTypes.get(i);
            int inputAmount = inputAmounts.get(i);
            output.put(inputType, -inputAmount);
        }
        
        if (producerType == AdvancedProducerType.RESEARCH_LAB) {
            // Special behavior for research labs
            if (location.getTerrainType().getName().equals("Mountains")) {
                int bonus = output.getOrDefault(primaryOutputType, 0) / 4;
                output.put(primaryOutputType, output.get(primaryOutputType) + bonus);
            }
        }
    }

    return output;
}
```

### Result Pattern
Used for error handling without exceptions:
```java
public Result<Boolean> canColonizeTile(int x, int y) {
    Tile tile = grid.getTileAt(x, y);

    if (tile == null) {
        return Result.failure("Invalid tile coordinates");
    }

    if (tile.isColonized()) {
        return Result.failure("Tile is already colonized");
    }

    if (!tile.isHabitable()) {
        return Result.failure("Tile terrain is not habitable");
    }

    if (!grid.canColonize(tile)) {
        return Result.failure("Tile must be adjacent to an already colonized tile");
    }

    // Resource checks...
    return Result.success(true);
}
```

## Additional Technical Features

### Notification System
The game implements a toast notification system for game events:

```java
public class NotificationManager extends VBox {
    private static final int MAX_VISIBLE_NOTIFICATIONS = 3;
    private final Queue<NotificationToast> activeNotifications = new LinkedList<>();
    private final Queue<NotificationEvents.GameNotificationEvent> pendingNotifications = new ConcurrentLinkedQueue<>();
    
    private void addNotification(NotificationEvents.GameNotificationEvent event) {
        pendingNotifications.add(event);

        if (!processingNotifications) {
            processingNotifications = true;
            processNextNotification();
        }
    }
    
    private void processNextNotification() {
        if (pendingNotifications.isEmpty() || activeNotifications.size() >= MAX_VISIBLE_NOTIFICATIONS) {
            processingNotifications = false;
            return;
        }

        NotificationEvents.GameNotificationEvent event = pendingNotifications.poll();
        NotificationToast toast = new NotificationToast(
                event.getTitle(),
                event.getMessage(),
                event.getType(),
                event.getDurationMs()
        );

        activeNotifications.add(toast);
        getChildren().addFirst(toast);

        toast.show(() -> {
            activeNotifications.remove(toast);
            getChildren().remove(toast);
            processNextNotification();
        });
    }
}
```

### Thread Safety and Performance
The application addresses thread safety concerns with several mechanisms:

```java
// Thread-safe event handling with Platform.runLater()
eventBus.subscribe(TileEvents.TileUpdatedEvent.class, event -> 
    Platform.runLater(() -> renderTile(event.getTile())));

// Concurrent collections for thread safety
private final Queue<NotificationEvents.GameNotificationEvent> pendingNotifications = 
    new ConcurrentLinkedQueue<>();

// Synchronized singleton access
public static synchronized EventBus getInstance() {
    if (instance == null) {
        instance = new EventBus();
    }
    return instance;
}
```

### Rendering Optimization
The map rendering system implements several optimizations:

```java
// Frustum culling for hexagons
private boolean isInView(double centerX, double centerY, double radius) {
    double minX = -translateX / scale;
    double minY = -translateY / scale;
    double maxX = (getWidth() - translateX) / scale;
    double maxY = (getHeight() - translateY) / scale;
    
    return !(centerX + radius < minX || centerX - radius > maxX || 
             centerY + radius < minY || centerY - radius > maxY);
}

// Performance monitoring
private long lastRenderTimeNs = 0;
private int visibleHexagons = 0;
private int totalHexagons = 0;

public void draw() {
    long startTime = System.nanoTime();
    // Drawing code...
    lastRenderTimeNs = System.nanoTime() - startTime;
    debugOverlay.setRenderStats(visibleHexagons, totalHexagons, lastRenderTimeNs / 1_000_000.0);
}
```
