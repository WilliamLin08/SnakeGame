import javax.swing.*;
// Import statements organized by category
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.function.Supplier;
import javax.swing.*;

/**
 * Main Snake Game Application
 * 
 * This is the primary class that manages the entire Snake game application.
 * It handles navigation between different game modes and panels, including:
 * - Map selection
 * - Single player game
 * - Multiplayer mode (two players)
 * - Timed mode (60-second challenge)
 * - VS AI mode (player vs computer)
 * - Settings panel (theme configuration)
 * 
 * The class extends JFrame to provide the main window container for all game components.
 * 
 * @author Snake Game Development Team
 * @version 2.0
 */
public class SnakeGame extends JFrame {
    
    // References to current game panels for theme updates
    private DuoGamePanel currentDuoPanel;
    private TimedGamePanel currentTimedPanel;
    private VSAIGamePanel currentVSAIPanel;

    /**
     * Constructor for the main Snake Game window.
     * Initializes the game window with default settings and displays the map selection panel.
     */
    public SnakeGame() {
        setTitle("Snake Game - Enhanced Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        showMapSelectPanel();
    }

    // Loading screen durations for different panels
    private static final int SETTINGS_LOADING_DURATION = 1500;
    private static final int MAP_SELECT_LOADING_DURATION = 1200;
    private static final int DUO_GAME_LOADING_DURATION = 1800;
    private static final int TIMED_GAME_LOADING_DURATION = 1600;
    private static final int VSAI_GAME_LOADING_DURATION = 2000;

    /**
     * Displays the settings panel where users can configure game themes.
     * Shows a loading screen before transitioning to the settings panel.
     */
    public void showSettingsPanel() {
        showPanelWithLoading("Loading Settings...", SETTINGS_LOADING_DURATION, 
            () -> new SettingsPanel(this));
    }

    /**
     * Displays the main map selection panel.
     * Shows a loading screen before transitioning to the map selection panel.
     * This is the central hub where players can choose between different game modes.
     */
    public void showMapSelectPanel() {
        showLoadingScreen("Loading Maps...", MAP_SELECT_LOADING_DURATION, () -> {
            clearAllPanelReferences();
            showPanel(new MapSelectPanel(this));
        });
    }

    /**
     * Launches the multiplayer game mode for two players.
     * Shows a loading screen before starting the multiplayer game.
     */
    public void showDuoGamePanel() {
        showLoadingScreen("Loading Multiplayer Game...", DUO_GAME_LOADING_DURATION, () -> {
            clearOtherPanelReferences("duo");
            currentDuoPanel = new DuoGamePanel(this);
            showPanel(currentDuoPanel);
        });
    }

    /**
     * Launches the timed game mode.
     * Shows a loading screen before starting the timed game.
     */
    public void showTimedGamePanel() {
        showLoadingScreen("Loading Timed Game...", TIMED_GAME_LOADING_DURATION, () -> {
            clearOtherPanelReferences("timed");
            currentTimedPanel = new TimedGamePanel(this);
            showPanel(currentTimedPanel);
        });
    }

    /**
     * Launches the VS AI game mode.
     * Shows a loading screen before starting the AI game.
     */
    public void showVSAIGamePanel() {
        showLoadingScreen("Loading AI Opponent...", VSAI_GAME_LOADING_DURATION, () -> {
            clearOtherPanelReferences("vsai");
            currentVSAIPanel = new VSAIGamePanel(this);
            showPanel(currentVSAIPanel);
        });
    }

    /**
     * Helper method to show a panel with loading screen using a panel factory.
     */
    private void showPanelWithLoading(String message, int duration, java.util.function.Supplier<JPanel> panelFactory) {
        showLoadingScreen(message, duration, () -> showPanel(panelFactory.get()));
    }

    /**
     * Helper method to display a panel and configure the window.
     */
    private void showPanel(JPanel panel) {
        getContentPane().removeAll();
        add(panel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        panel.requestFocusInWindow();
    }

    /**
     * Clears all panel references when returning to map selection.
     */
    private void clearAllPanelReferences() {
        currentDuoPanel = null;
        currentTimedPanel = null;
        currentVSAIPanel = null;
    }

    /**
     * Clears panel references except for the specified type.
     */
    private void clearOtherPanelReferences(String keepType) {
        if (!"duo".equals(keepType)) currentDuoPanel = null;
        if (!"timed".equals(keepType)) currentTimedPanel = null;
        if (!"vsai".equals(keepType)) currentVSAIPanel = null;
    }
    
    /**
     * Updates themes for all currently active game panels.
     */
    public void updateActiveGamePanelThemes() {
        if (currentDuoPanel != null) {
            currentDuoPanel.updateTheme();
        }
        if (currentTimedPanel != null) {
            currentTimedPanel.updateTheme();
        }
        if (currentVSAIPanel != null) {
            currentVSAIPanel.updateTheme();
        }
    }

    /**
     * Displays the main single-player game panel with the selected map.
     * Shows a loading screen before starting the game.
     * This method handles both static maps and maps with randomly generated obstacles.
     * 
     * For maps with random obstacles:
     * 1. Generates random obstacle positions while avoiding the snake's starting area
     * 2. Ensures obstacles don't spawn on the initial snake body (center 3 cells)
     * 3. Creates a new map instance with the generated obstacles
     * 
     * @param selectedMap The map configuration chosen by the player
     */
    public void showGamePanel(GameMap selectedMap) {
        String loadingMessage = selectedMap.randomObstacles ? 
            "Generating Map..." : "Loading Game...";
        
        showLoadingScreen(loadingMessage, 1400, () -> {
            getContentPane().removeAll();
            GameMap mapToUse = selectedMap;
            
            // Generate random obstacles if the map requires them
            if (selectedMap.randomObstacles) {
                mapToUse = generateRandomObstacleMap(selectedMap);
            }
            
            // Create and display the game panel
            GamePanel gamePanel = new GamePanel(this, mapToUse);
            add(gamePanel, BorderLayout.CENTER);
            pack();
            setLocationRelativeTo(null);
            setVisible(true);
            gamePanel.requestFocusInWindow();
        });
    }
    
    // Constants for obstacle generation
    private static final int SNAKE_START_AREA_SIZE = 3;
    private static final int MAX_OBSTACLE_GENERATION_ATTEMPTS = 1000;
    
    /**
     * Generates a new map with randomly placed obstacles.
     * Uses an optimized algorithm to avoid infinite loops and ensure efficient generation.
     * 
     * @param originalMap The base map configuration
     * @return A new GameMap with randomly generated obstacles
     */
    private GameMap generateRandomObstacleMap(GameMap originalMap) {
        Random random = new Random();
        Set<Point> reservedPositions = createReservedPositions(originalMap);
        List<Point> obstacles = generateObstacles(originalMap, random, reservedPositions);
        
        return new GameMap(originalMap.name, originalMap.width, originalMap.height, 
                          obstacles, originalMap.description, false, 0);
    }
    
    /**
     * Creates the set of reserved positions where obstacles cannot be placed.
     */
    private Set<Point> createReservedPositions(GameMap map) {
        Set<Point> reserved = new HashSet<>();
        int centerX = map.width / 2;
        int centerY = map.height / 2;
        
        // Reserve the snake's starting positions (center 3 cells)
        for (int i = 0; i < SNAKE_START_AREA_SIZE; i++) {
            reserved.add(new Point(centerX, centerY + i));
        }
        
        return reserved;
    }
    
    /**
     * Generates obstacles using an optimized algorithm that avoids infinite loops.
     */
    private List<Point> generateObstacles(GameMap map, Random random, Set<Point> reservedPositions) {
        List<Point> obstacles = new ArrayList<>();
        int totalCells = map.width * map.height;
        int maxObstacles = Math.min(map.obstacleCount, totalCells - reservedPositions.size());
        
        // Use a more efficient approach for high obstacle density
        if (maxObstacles > totalCells * 0.3) {
            return generateObstaclesByExclusion(map, random, reservedPositions, maxObstacles);
        } else {
            return generateObstaclesByInclusion(map, random, reservedPositions, maxObstacles);
        }
    }
    
    /**
     * Generates obstacles by randomly selecting positions (efficient for low density).
     */
    private List<Point> generateObstaclesByInclusion(GameMap map, Random random, 
                                                    Set<Point> reservedPositions, int maxObstacles) {
        List<Point> obstacles = new ArrayList<>();
        int attempts = 0;
        
        while (obstacles.size() < maxObstacles && attempts < MAX_OBSTACLE_GENERATION_ATTEMPTS) {
            int x = random.nextInt(map.width);
            int y = random.nextInt(map.height);
            Point newObstacle = new Point(x, y);
            
            if (!reservedPositions.contains(newObstacle)) {
                obstacles.add(newObstacle);
                reservedPositions.add(newObstacle);
            }
            attempts++;
        }
        
        return obstacles;
    }
    
    /**
     * Generates obstacles by creating all positions and removing reserved ones (efficient for high density).
     */
    private List<Point> generateObstaclesByExclusion(GameMap map, Random random, 
                                                    Set<Point> reservedPositions, int maxObstacles) {
        List<Point> allPositions = new ArrayList<>();
        
        // Create all possible positions
        for (int x = 0; x < map.width; x++) {
            for (int y = 0; y < map.height; y++) {
                Point pos = new Point(x, y);
                if (!reservedPositions.contains(pos)) {
                    allPositions.add(pos);
                }
            }
        }
        
        // Shuffle and take the first maxObstacles positions
        Collections.shuffle(allPositions, random);
        return allPositions.subList(0, Math.min(maxObstacles, allPositions.size()));
    }
    
    /**
     * Displays a loading screen with animation before executing a callback.
     * This method provides visual feedback during panel transitions.
     * 
     * @param message The loading message to display
     * @param duration Loading duration in milliseconds
     * @param onComplete Callback to execute when loading is complete
     */
    private void showLoadingScreen(String message, int duration, Runnable onComplete) {
        getContentPane().removeAll();
        LoadingPanel loadingPanel = new LoadingPanel(this, message, duration, onComplete);
        add(loadingPanel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        loadingPanel.requestFocusInWindow();
    }
    
    /**
     * Main entry point for the Snake Game application.
     * Uses EventQueue.invokeLater to ensure the GUI is created on the Event Dispatch Thread.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Set system look and feel for better OS integration
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fall back to default look and feel if system L&F fails
            System.err.println("Could not set system look and feel: " + e.getMessage());
        }
        
        // Launch the game on the Event Dispatch Thread
        EventQueue.invokeLater(SnakeGame::new);
    }
}


/**
 * Map Selection Panel - Main Menu Interface
 * 
 * This panel serves as the central hub for the Snake Game, providing access to:
 * - Different map types (Classic, Obstacles)
 * - Various game modes (Single, Multiplayer, Timed, VS AI)
 * - Game settings and theme configuration
 * 
 * The panel uses a clean, card-based design with a 2-column grid layout
 * for easy navigation and visual appeal.
 */
class MapSelectPanel extends JPanel {
    
    // UI Design Constants - Enhanced for modern appearance
    private static final Color BACKGROUND_COLOR = new Color(15, 23, 42);  // Dark slate background
    private static final Color CARD_BACKGROUND = new Color(30, 41, 59);   // Slate card background
    private static final Color CARD_HOVER_COLOR = new Color(51, 65, 85);  // Lighter slate on hover
    private static final Color ACCENT_COLOR = new Color(59, 130, 246);    // Blue accent
    private static final Color ACCENT_HOVER = new Color(37, 99, 235);     // Darker blue on hover
    private static final Color TITLE_COLOR = new Color(248, 250, 252);    // Light text
    private static final Color DESCRIPTION_COLOR = new Color(148, 163, 184); // Muted text
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 32);
    private static final Font CARD_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font CARD_DESC_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    
    /**
     * Available game maps with different difficulty levels and features.
     * Classic: Simple empty map for beginners
     * Obstacles: Challenging map with randomly generated obstacles
     */
    private static final List<GameMap> AVAILABLE_MAPS = new ArrayList<>(Arrays.asList(
            new GameMap("Classic", 25, 25, new ArrayList<>(), 
                       "Simple map suitable for beginners - perfect for learning the basics"),
            new GameMap("Obstacles", 25, 25, new ArrayList<>(), 
                       "Challenging map full of obstacles - test your skills!", true, 15)
    ));

    /**
     * Constructor for the Map Selection Panel.
     * Creates a visually appealing interface with cards for each game mode and map.
     * 
     * @param parent Reference to the main SnakeGame window for navigation
     */
    public MapSelectPanel(SnakeGame parent) {
        initializeLayout();
        createTitleSection();
        createGameModeCards(parent);
        setPreferredSize(new Dimension(700, 700));
    }
    
    /**
     * Initializes the basic layout and styling for the panel.
     */
    private void initializeLayout() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
    }
    
    /**
     * Creates and adds an enhanced title section with subtitle and modern styling.
     */
    private void createTitleSection() {
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 30, 0));
        
        // Main title with gradient effect
        JLabel titleLabel = new JLabel("🎮 Snake Game", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Create gradient paint for text
                GradientPaint gradient = new GradientPaint(
                    0, 0, TITLE_COLOR,
                    0, getHeight(), ACCENT_COLOR
                );
                g2d.setPaint(gradient);
                g2d.setFont(getFont());
                
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(getText(), x, y);
                
                g2d.dispose();
            }
        };
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setOpaque(false);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Choose your adventure", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        subtitleLabel.setForeground(DESCRIPTION_COLOR);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        add(titlePanel, BorderLayout.NORTH);
    }
    
    /**
     * Creates the main content area with cards for all game modes and maps.
     * Uses a 2-column grid layout for optimal space utilization.
     * 
     * @param parent Reference to the main game window for navigation callbacks
     */
    private void createGameModeCards(SnakeGame parent) {
        // Create the main content panel with grid layout
        JPanel centerPanel = new JPanel(new GridLayout(0, 2, 32, 24));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 60));

        // Add map cards for single-player modes
        for (GameMap map : AVAILABLE_MAPS) {
            JPanel mapPanel = createMapCard(map, parent);
            centerPanel.add(mapPanel);
        }

        // Add special game mode cards
        centerPanel.add(createModeCard("Multiplayer Mode", 
                                      "Battle with friends on the same screen", 
                                      "Start Game", 
                                      e -> parent.showDuoGamePanel()));
        
        centerPanel.add(createModeCard("Timed Mode", 
                                      "Get the highest score in 60 seconds", 
                                      "Start Game", 
                                      e -> parent.showTimedGamePanel()));
        
        centerPanel.add(createModeCard("VS AI Mode", 
                                      "Challenge an intelligent AI opponent", 
                                      "Start Game", 
                                      e -> parent.showVSAIGamePanel()));
        
        centerPanel.add(createModeCard("Settings", 
                                      "Configure themes and game options", 
                                      "Open Settings", 
                                      e -> parent.showSettingsPanel()));

        add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * Creates a card panel for a specific game map.
     * Each card displays the map name, description, and a start button.
     * 
     * @param map The game map to create a card for
     * @param parent Reference to the main game window for navigation
     * @return A styled JPanel representing the map card
     */
    private JPanel createMapCard(GameMap map, SnakeGame parent) {
        return createCard(map.name, map.description, "Start Game", 
                         e -> parent.showGamePanel(map));
    }

    /**
     * Creates a card panel for a specific game mode.
     * Each card displays the mode name, description, and an action button.
     * 
     * @param title The title of the game mode
     * @param description Brief description of the game mode
     * @param buttonText Text to display on the action button
     * @param listener Action to perform when the button is clicked
     * @return A styled JPanel representing the mode card
     */
    private JPanel createModeCard(String title, String description, String buttonText, ActionListener listener) {
        return createCard(title, description, buttonText, listener);
    }
    
    /**
     * Creates a standardized card panel with modern styling including gradients and shadows.
     * This method reduces code duplication and ensures visual consistency.
     * 
     * @param title The main title text for the card
     * @param description The description text for the card
     * @param buttonText The text for the action button
     * @param listener The action to perform when the button is clicked
     * @return A fully styled card panel with modern design
     */
    private JPanel createCard(String title, String description, String buttonText, ActionListener listener) {
        // Create main panel with custom painting for gradient background
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, CARD_BACKGROUND,
                    0, getHeight(), new Color(CARD_BACKGROUND.getRed() + 10, 
                                            CARD_BACKGROUND.getGreen() + 10, 
                                            CARD_BACKGROUND.getBlue() + 10)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                
                // Add subtle border
                g2d.setColor(new Color(71, 85, 105, 100));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                
                g2d.dispose();
            }
        };
        
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 20, 24, 20));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add hover effect
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                panel.repaint();
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                panel.repaint();
            }
        });

        // Create and style the title label with icon
        JLabel titleLabel = new JLabel("● " + title);
        titleLabel.setFont(CARD_TITLE_FONT);
        titleLabel.setForeground(TITLE_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create and style the description label
        JLabel descLabel = new JLabel("<html><div style='text-align: center; line-height: 1.4;'>" + description + "</div></html>");
        descLabel.setFont(CARD_DESC_FONT);
        descLabel.setForeground(DESCRIPTION_COLOR);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descLabel.setBorder(BorderFactory.createEmptyBorder(12, 0, 20, 0));

        // Create and style the action button
        JButton actionButton = createStyledButton(buttonText, listener);

        // Add components to panel
        panel.add(titleLabel);
        panel.add(descLabel);
        panel.add(actionButton);

        return panel;
    }
    
    /**
     * Creates a modern styled button with gradient background and smooth animations.
     * 
     * @param text The text to display on the button
     * @param listener The action to perform when clicked
     * @return A beautifully styled JButton
     */
    private JButton createStyledButton(String text, ActionListener listener) {
        JButton button = new JButton(text) {
            private boolean isHovered = false;
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create gradient background
                Color startColor = isHovered ? ACCENT_HOVER : ACCENT_COLOR;
                Color endColor = new Color(
                    Math.max(0, startColor.getRed() - 20),
                    Math.max(0, startColor.getGreen() - 20),
                    Math.max(0, startColor.getBlue() - 20)
                );
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, startColor,
                    0, getHeight(), endColor
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                // Add subtle shadow effect when hovered
                if (isHovered) {
                    g2d.setColor(new Color(0, 0, 0, 30));
                    g2d.fillRoundRect(2, 2, getWidth() - 2, getHeight() - 2, 8, 8);
                }
                
                g2d.dispose();
                super.paintComponent(g);
            }
            
            @Override
            public void setContentAreaFilled(boolean b) {
                // Override to prevent default button painting
            }
        };
        
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFont(BUTTON_FONT);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(160, 42));
        button.setPreferredSize(new Dimension(160, 42));
        button.addActionListener(listener);
        
        // Add smooth hover animations
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                // Use reflection to access the isHovered field
                try {
                    java.lang.reflect.Field field = button.getClass().getDeclaredField("isHovered");
                    field.setAccessible(true);
                    field.setBoolean(button, true);
                } catch (Exception e) {
                    // Fallback: just repaint
                }
                button.repaint();
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                // Use reflection to access the isHovered field
                try {
                    java.lang.reflect.Field field = button.getClass().getDeclaredField("isHovered");
                    field.setAccessible(true);
                    field.setBoolean(button, false);
                } catch (Exception e) {
                    // Fallback: just repaint
                }
                button.repaint();
            }
        });
        
        return button;
    }
}

/**
 * Game Map Configuration Class
 * 
 * Represents a game map with its properties and settings.
 * Maps can be static (with predefined obstacles) or dynamic (with randomly generated obstacles).
 * 
 * Key features:
 * - Configurable dimensions (width x height)
 * - Static or dynamic obstacle placement
 * - Descriptive information for UI display
 * - Support for different difficulty levels
 */
class GameMap {
    /** The display name of the map */
    final String name;
    
    /** Map dimensions in grid cells */
    final int width, height;
    
    /** List of obstacle positions (for static maps) */
    final List<Point> obstacles;
    
    /** Description text shown in the map selection UI */
    final String description;
    
    /** Whether obstacles should be randomly generated */
    final boolean randomObstacles;
    
    /** Number of obstacles to generate (for random maps) */
    final int obstacleCount;

    /**
     * Constructor for static maps with predefined obstacles.
     * 
     * @param name Display name of the map
     * @param width Map width in grid cells
     * @param height Map height in grid cells
     * @param obstacles List of predefined obstacle positions
     * @param description Description text for the UI
     */
    public GameMap(String name, int width, int height, List<Point> obstacles, String description) {
        this(name, width, height, obstacles, description, false, 0);
    }
    
    /**
     * Constructor for maps with configurable obstacle generation.
     * 
     * @param name Display name of the map
     * @param width Map width in grid cells
     * @param height Map height in grid cells
     * @param obstacles List of predefined obstacle positions (ignored if randomObstacles is true)
     * @param description Description text for the UI
     * @param randomObstacles Whether to generate random obstacles
     * @param obstacleCount Number of random obstacles to generate
     */
    public GameMap(String name, int width, int height, List<Point> obstacles, 
                   String description, boolean randomObstacles, int obstacleCount) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.obstacles = new ArrayList<>(obstacles); // Defensive copy
        this.description = description;
        this.randomObstacles = randomObstacles;
        this.obstacleCount = obstacleCount;
    }
}



/**
 * Main Game Panel Class
 * 
 * This class handles the core Snake game logic, rendering, and user interaction.
 * It manages the game state, snake movement, collision detection, scoring, and UI elements.
 * 
 * Key Features:
 * - Snake movement and growth mechanics
 * - Food generation and consumption
 * - Obstacle collision detection
 * - Progressive difficulty scaling
 * - Theme-based visual rendering
 * - Pause/resume functionality
 * - Lives system with game over handling
 * 
 * Game Controls:
 * - Arrow Keys: Move snake (Up, Down, Left, Right)
 * - Space: Pause/Resume game
 * - Enter: Start game from welcome screen
 * - R: Restart game after game over
 */
class GamePanel extends JPanel implements ActionListener {
    // === GAME CONFIGURATION CONSTANTS ===
    /** Size of each cell in the game grid (pixels) */
    private static final int CELL_SIZE = 20;
    
    /** Initial game speed delay (milliseconds between moves) */
    private static final int INITIAL_DELAY = 150;
    
    /** Minimum delay for maximum difficulty */
    private static final int MIN_DELAY = 50;
    
    /** Points awarded per food eaten */
    private static final int POINTS_PER_FOOD = 10;
    
    /** Initial number of lives */
    private static final int INITIAL_LIVES = 3;
    
    /** Score threshold for level progression */
    private static final int LEVEL_PROGRESSION_THRESHOLD = 50;
    
    /** Speed increase per level */
    private static final int SPEED_INCREASE_PER_LEVEL = 10;
    
    /** UI element dimensions and positions */
    private static final int BUTTON_WIDTH = 60;
    private static final int BUTTON_HEIGHT = 40;
    private static final int EXIT_BUTTON_WIDTH = 80;
    private static final int EXIT_BUTTON_HEIGHT = 35;
    private static final int LIFE_INDICATOR_SIZE = 15;
    private static final int LIFE_INDICATOR_SPACING = 25;
    
    /** Available game maps for selection */
    private static final List<GameMap> maps = Arrays.asList(
            new GameMap("Classic", 25, 25, new ArrayList<>(), "Simple map suitable for beginners"),
            new GameMap("Obstacles", 25, 25, new ArrayList<>(), "Challenging map full of obstacles", true, 15)
    );

    // === GAME STATE VARIABLES ===
    /** The currently selected game map */
    private GameMap selectedMap;
    
    /** Reference to the main game window */
    private final SnakeGame parent;
    
    /** Snake body segments (head is at index 0) */
    private ArrayList<Point> snake;
    
    /** Current food position */
    private Point food;
    
    /** Current food type with special effects */
    private FoodType currentFoodType;
    
    /** Current movement direction (0=Up, 1=Right, 2=Down, 3=Left) */
    private int direction;
    
    /** Whether the game is currently running */
    private boolean running;
    
    /** Whether the game is paused */
    private boolean paused;
    
    /** Whether the game has been started by the player */
    private boolean gameStarted;
    
    /** Current player score */
    private int score;
    
    /** Remaining lives */
    private int lives;
    
    /** Highest score achieved in this session */
    private int highScore;
    
    /** Game timer for controlling snake movement */
    private javax.swing.Timer timer;
    
    /** Random number generator for food placement */
    private Random random;
    
    /** Current movement delay (controls game speed) */
    private int currentDelay;
    
    /** Current difficulty level */
    private int level;
    
    /** Current visual theme */
    private Theme currentTheme;
    
    /** Sound manager for game audio */
    private SoundManager soundManager;
    
    /** Animation manager for smooth effects */
    private AnimationManager animationManager;
    
    // === FOOD EFFECT STATE ===
    /** End time for speed effect */
    private long speedEffectEndTime;
    
    /** End time for slow effect */
    private long slowEffectEndTime;
    
    /** Whether speed effect is currently active */
    private boolean hasSpeedEffect;
    
    /** Whether slow effect is currently active */
    private boolean hasSlowEffect;
    
    /** Original delay before effects were applied */
    private int originalDelay;
    
    // === UI COMPONENTS ===
    /** Font for main game text */
    private Font gameFont;
    
    /** Font for instruction text */
    private Font instructionFont;
    
    /** Button to show game instructions */
    private JButton instructionButton;
    
    /** Dialog window for displaying instructions */
    private JDialog instructionDialog;
    
    /** Button to exit to map selection */
    private JButton exitButton;

    /**
     * Constructor for GamePanel.
     * Initializes the game panel with the specified map and sets up all UI components.
     * 
     * @param parent Reference to the main SnakeGame window for navigation
     * @param selectedMap The game map to play on
     */
    public GamePanel(SnakeGame parent, GameMap selectedMap) {
        this.parent = parent;
        this.selectedMap = selectedMap;
        this.currentTheme = Settings.getInstance().getCurrentTheme();
        
        // Initialize sound manager
        this.soundManager = SoundManager.getInstance();
        this.soundManager.setTheme(currentTheme.name);
        this.soundManager.setSoundEnabled(Settings.getInstance().isSoundEnabled());
        
        // Initialize animation manager
        this.animationManager = new AnimationManager();

        // Configure panel properties
        setPreferredSize(new Dimension(selectedMap.width * CELL_SIZE, selectedMap.height * CELL_SIZE));
        setBackground(currentTheme.backgroundColor);
        setFocusable(true);
        setLayout(null);

        // Initialize fonts and utilities
        gameFont = new Font("SansSerif", Font.BOLD, 14);
        instructionFont = new Font("SansSerif", Font.PLAIN, 12);
        random = new Random();

        // Create and configure UI buttons
        setupUIButtons();
        createInstructionDialog();

        // Set up input handling and initialize game state
        addKeyListener(new GameKeyAdapter());
        initGame();
        requestFocusInWindow();
    }
    
    /**
     * Sets up the UI buttons (instruction and exit buttons) with proper styling and positioning.
     */
    private void setupUIButtons() {
        // Instruction button - shows game controls and rules
        instructionButton = createGameButton("?", 
            selectedMap.width * CELL_SIZE - 70, 
            selectedMap.height * CELL_SIZE - 50, 
            BUTTON_WIDTH, BUTTON_HEIGHT,
            new Color(200, 200, 200, 180),
            e -> showInstructionDialog());
        add(instructionButton);

        // Exit button - returns to map selection
        exitButton = createGameButton("exit", 
            selectedMap.width * CELL_SIZE - 140, 
            selectedMap.height * CELL_SIZE - 45, 
            EXIT_BUTTON_WIDTH, EXIT_BUTTON_HEIGHT,
            Color.WHITE,
            e -> exitToMapSelect());
        
        // Add hover effects for better user experience
        addHoverEffect(exitButton, Color.WHITE, new Color(255, 255, 255, 220));
        add(exitButton);
    }
    
    /**
     * Creates a standardized game button with consistent styling.
     */
    private JButton createGameButton(String text, int x, int y, int width, int height, 
                                   Color foregroundColor, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBounds(x, y, width, height);
        button.setFocusable(false);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setBorder(null);
        button.setBackground(new Color(0, 0, 0, 0));
        button.setForeground(foregroundColor);
        button.addActionListener(listener);
        return button;
    }
    
    /**
     * Adds hover effect to a button.
     */
    private void addHoverEffect(JButton button, Color normalColor, Color hoverColor) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(hoverColor);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(normalColor);
            }
        });
    }

    /**
     * Exits the current game and returns to the map selection screen.
     * This method is called when the player clicks the exit button.
     */
    private void exitToMapSelect() {
        parent.showMapSelectPanel();
    }

    /**
     * Shows a dialog for map selection (legacy method - not currently used).
     * Displays available maps and allows the player to choose one.
     */
    private void selectMap() {
        String[] mapNames = maps.stream().map(m -> m.name).toArray(String[]::new);
        int mapIndex = JOptionPane.showOptionDialog(
                null, "Please select a map", "Map Selection",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, mapNames, mapNames[0]
        );
        selectedMap = maps.get(mapIndex == -1 ? 0 : mapIndex);
    }

    /**
     * Creates the instruction dialog window with game rules and controls.
     * The dialog is modal and shows comprehensive game information including:
     * - Game objective and scoring
     * - Control scheme
     * - Game rules and mechanics
     * - Tips for better gameplay
     */
    private void createInstructionDialog() {
        instructionDialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "🎮 Game Instructions", true);
        instructionDialog.setSize(500, 400);
        instructionDialog.setLocationRelativeTo(this);
        instructionDialog.setResizable(false);
        instructionDialog.getContentPane().setBackground(new Color(15, 23, 42));

        // Create main panel with padding and modern styling
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(15, 23, 42),
                    0, getHeight(), new Color(30, 41, 59)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Subtle border
                g2d.setColor(new Color(71, 85, 105, 100));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                
                g2d.dispose();
            }
        };
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        panel.setOpaque(false);

        // Create title label
        JLabel titleLabel = new JLabel("🎮 Snake Game Guide");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(248, 250, 252));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Create text area for instructions with modern styling
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setBackground(new Color(30, 41, 59));
        textArea.setForeground(new Color(226, 232, 240));
        textArea.setCaretColor(new Color(59, 130, 246));
        textArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Enhanced game instructions with better formatting
        String instructions =
                "🎯 OBJECTIVE\n" +
                "Control the snake to eat food and achieve the highest score possible!\n\n" +
                "🎮 CONTROLS\n" +
                "• Arrow Keys / WASD → Move snake\n" +
                "• Spacebar / P → Pause/Resume\n" +
                "• R → Restart game\n" +
                "• Shift + / → Show this help\n\n" +
                "📋 RULES\n" +
                "• Each food eaten = 10 points\n" +
                "• Avoid walls, obstacles, and yourself\n" +
                "• You have 3 lives total\n" +
                "• Speed increases with your score\n\n" +
                "💡 PRO TIPS\n" +
                "• Try different maps for unique challenges\n" +
                "• Plan your route to avoid collisions\n" +
                "• Higher levels = greater difficulty\n" +
                "• Use pause strategically to plan moves";

        textArea.setText(instructions);

        // Create custom scroll pane with dark theme
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBackground(new Color(30, 41, 59));
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(71, 85, 105), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        scrollPane.getViewport().setBackground(new Color(30, 41, 59));
        scrollPane.getVerticalScrollBar().setBackground(new Color(51, 65, 85));
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(100, 116, 139);
                this.trackColor = new Color(51, 65, 85);
            }
        });
        panel.add(scrollPane, BorderLayout.CENTER);

        // Create styled close button
        JButton closeButton = new JButton("✓ Got it!") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Button background with gradient
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(59, 130, 246),
                    0, getHeight(), new Color(37, 99, 235)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                // Subtle border
                g2d.setColor(new Color(29, 78, 216));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeButton.setForeground(Color.WHITE);
        closeButton.setOpaque(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setPreferredSize(new Dimension(120, 35));
        closeButton.addActionListener(e -> instructionDialog.setVisible(false));
        
        // Add hover effect
        closeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                closeButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        instructionDialog.add(panel);
        instructionDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
    }

    /**
     * Shows the instruction dialog and handles game pause state.
     * Automatically pauses the game when instructions are shown and
     * resumes if the game was not previously paused.
     */
    private void showInstructionDialog() {
        boolean wasPaused = paused;
        if (running && !paused) {
            paused = true;
        }
        instructionDialog.setVisible(true);
        if (running && !wasPaused) {
            paused = false;
        }
        this.requestFocusInWindow();
    }

    /**
     * Initializes a new game session.
     * Resets all game state variables to their starting values:
     * - Creates new snake at center position
     * - Resets score, lives, and level
     * - Sets initial game speed
     * - Starts the game timer
     */
    private void initGame() {
        snake = new ArrayList<>();
        resetSnake();
        score = 0;
        lives = 3;
        running = true;
        paused = true;
        gameStarted = false;
        level = 1;
        currentDelay = INITIAL_DELAY;
        originalDelay = INITIAL_DELAY;
        highScore = 0;
        
        // Initialize food effect state
        speedEffectEndTime = 0;
        slowEffectEndTime = 0;
        hasSpeedEffect = false;
        hasSlowEffect = false;
        currentFoodType = FoodType.NORMAL;

        // Initialize game timer
        if (timer != null) timer.stop();
        timer = new javax.swing.Timer(currentDelay, this);
        timer.start();
        
        // Start background music
        soundManager.playBackgroundMusic();
    }
    
    /**
     * Updates the current theme and refreshes sound effects accordingly.
     * Called when the theme is changed in settings.
     */
    public void updateTheme() {
        Theme newTheme = Settings.getInstance().getCurrentTheme();
        if (!newTheme.name.equals(currentTheme.name)) {
            currentTheme = newTheme;
            soundManager.setTheme(currentTheme.name);
            if (running) {
                soundManager.playBackgroundMusic(); // Restart background music with new theme
            }
            repaint(); // Refresh visual appearance
        }
        // Always sync sound enabled setting
        soundManager.setSoundEnabled(Settings.getInstance().isSoundEnabled());
        
        // Update theme for all active game panels
        parent.updateActiveGamePanelThemes();
    }

    /**
     * Resets the snake to its initial position and size.
     * Places the snake at the center of the map with 3 initial segments.
     * The snake starts moving upward (direction 0).
     */
    private void resetSnake() {
        snake.clear();
        int startX = selectedMap.width / 2;
        int startY = selectedMap.height / 2;
        
        // Create initial snake with 3 segments (head + 2 body segments)
        snake.add(new Point(startX, startY));     // Head
        snake.add(new Point(startX, startY + 1)); // Body segment 1
        snake.add(new Point(startX, startY + 2)); // Body segment 2
        direction = KeyEvent.VK_UP;
        generateFood();
    }

    /**
     * Generates a new food item at a random valid position.
     * Ensures the food doesn't spawn on the snake body or on obstacles.
     * Uses a do-while loop to keep trying until a valid position is found.
     * Also generates a random food type with special effects.
     */
    private void generateFood() {
        int x, y;
        do {
            x = random.nextInt(selectedMap.width);
            y = random.nextInt(selectedMap.height);
        } while (isOnSnake(x, y) || isObstacle(x, y));
        food = new Point(x, y);
        currentFoodType = FoodType.getRandomFoodType();
    }

    /**
     * Checks if the specified coordinates are occupied by any part of the snake.
     * 
     * @param x The x-coordinate to check
     * @param y The y-coordinate to check
     * @return true if the position is occupied by the snake, false otherwise
     */
    private boolean isOnSnake(int x, int y) {
        for (Point p : snake) {
            if (p.x == x && p.y == y) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the specified coordinates contain an obstacle.
     * 
     * @param x The x-coordinate to check
     * @param y The y-coordinate to check
     * @return true if the position contains an obstacle, false otherwise
     */
    private boolean isObstacle(int x, int y) {
        for (Point obs : selectedMap.obstacles) {
            if (obs.x == x && obs.y == y) return true;
        }
        return false;
    }

    /**
     * Timer action handler - called periodically to update the game state.
     * Moves the snake if the game is running and not paused, then repaints the screen.
     * Also updates animations and food effects.
     * 
     * @param e The ActionEvent from the timer
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !paused) {
            updateFoodEffects();
            move();
        }
        animationManager.update();
        repaint();
    }

    /**
     * Handles snake movement and collision detection.
     * This is the core game logic method that:
     * 1. Calculates the new head position based on current direction
     * 2. Checks for collisions with walls, obstacles, and self
     * 3. Handles food consumption and snake growth
     * 4. Updates score and difficulty
     */
    private void move() {
        Point head = snake.get(0);
        Point newHead = new Point(head);
        
        // Calculate new head position based on direction
        switch (direction) {
            case KeyEvent.VK_UP: newHead.y--; break;
            case KeyEvent.VK_DOWN: newHead.y++; break;
            case KeyEvent.VK_LEFT: newHead.x--; break;
            case KeyEvent.VK_RIGHT: newHead.x++; break;
        }
        
        // Check wall collision
        if (newHead.x < 0 || newHead.x >= selectedMap.width || newHead.y < 0 || newHead.y >= selectedMap.height) {
            gameOver();
            return;
        }
        
        // Check self collision (skip head at index 0)
        for (int i = 1; i < snake.size(); i++) {
            if (snake.get(i).equals(newHead)) {
                gameOver();
                return;
            }
        }
        
        // Check obstacle collision
        for (Point obs : selectedMap.obstacles) {
            if (obs.x == newHead.x && obs.y == newHead.y) {
                gameOver();
                return;
            }
        }
        
        // Add new head to snake
        snake.add(0, newHead);
        
        // Check food consumption
        if (newHead.x == food.x && newHead.y == food.y) {
            // Apply food effects and scoring
            applyFoodEffect(currentFoodType);
            score += currentFoodType.points;
            if (score > highScore) highScore = score;
            
            // Play sound and visual effects
            soundManager.playEatSound();
            animationManager.startFoodConsumptionAnimation();
            
            // Generate new food and increase difficulty
            generateFood();
            increaseDifficulty();
        } else {
            // Remove tail if no food was eaten (maintains snake length)
            snake.remove(snake.size() - 1);
        }
    }

    /**
     * Increases game difficulty based on current score.
     * Every 50 points increases the level, which decreases the movement delay
     * (making the snake move faster). The delay has a minimum threshold.
     */
    private void increaseDifficulty() {
        int newLevel = (score / 50) + 1;
        if (newLevel > level) {
            level = newLevel;
            currentDelay = Math.max(MIN_DELAY, INITIAL_DELAY - ((level - 1) * 10));
            timer.setDelay(currentDelay);
            soundManager.playLevelUpSound(); // Play level up sound
        }
    }

    /**
     * Checks if a direction change is safe (won't cause immediate self-collision).
     * Prevents the snake from moving directly into its neck segment.
     * 
     * @param newDirection The proposed new direction (KeyEvent constant)
     * @return true if the direction change is safe, false if it would cause immediate collision
     */
    private boolean isSafeDirectionChange(int newDirection) {
        if (snake.size() < 2) return true; // Safe if snake is too short to collide with itself
        
        Point head = snake.get(0);
        Point neck = snake.get(1);
        Point newHead = new Point(head);
        
        // Calculate where the head would be with the new direction
        switch (newDirection) {
            case KeyEvent.VK_UP: newHead.y--; break;
            case KeyEvent.VK_DOWN: newHead.y++; break;
            case KeyEvent.VK_LEFT: newHead.x--; break;
            case KeyEvent.VK_RIGHT: newHead.x++; break;
        }
        
        // Check if the new head position would be the same as the neck (immediate collision)
        return !newHead.equals(neck);
    }

    /**
     * Handles game over logic when the snake collides with something.
     * Decrements lives and either resets the snake (if lives remain) or ends the game.
     */
    private void gameOver() {
        lives--;
        soundManager.playGameOverSound(); // Play game over sound
        if (lives > 0) {
            resetSnake();
        } else {
            running = false;
            timer.stop();
            soundManager.stopBackgroundMusic(); // Stop background music when game ends
        }
    }

    /**
     * Exits the game and closes the window (legacy method - not currently used).
     */
    private void exitToMenu() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();
        }
    }
    
    /**
     * Applies the effect of the consumed food type.
     * 
     * @param foodType The type of food that was consumed
     */
    private void applyFoodEffect(FoodType foodType) {
        long currentTime = System.currentTimeMillis();
        
        switch (foodType) {
            case SPEED:
                hasSpeedEffect = true;
                speedEffectEndTime = currentTime + foodType.effectDuration;
                if (!hasSlowEffect) {
                    currentDelay = Math.max(MIN_DELAY, originalDelay / 2);
                    timer.setDelay(currentDelay);
                }
                animationManager.triggerScreenShake(0.3f, 0.2f);
                break;
                
            case SLOW:
                hasSlowEffect = true;
                slowEffectEndTime = currentTime + foodType.effectDuration;
                if (!hasSpeedEffect) {
                    currentDelay = originalDelay * 2;
                    timer.setDelay(currentDelay);
                }
                break;
                
            case MEGA:
                // Mega food gives extra points (already handled in scoring)
                animationManager.triggerScreenShake(0.5f, 0.3f);
                break;
                
            case SHRINK:
                // Shrink the snake by removing tail segments
                if (snake.size() > 3) {
                    snake.remove(snake.size() - 1);
                    if (snake.size() > 3) {
                        snake.remove(snake.size() - 1);
                    }
                }
                break;
                
            case BONUS:
                // Bonus food gives extra points (already handled in scoring)
                animationManager.triggerScreenShake(0.2f, 0.15f);
                break;
                
            case NORMAL:
            default:
                // No special effect
                break;
        }
    }
    
    /**
     * Updates and manages active food effects.
     * Removes expired effects and restores normal game speed.
     */
    private void updateFoodEffects() {
        long currentTime = System.currentTimeMillis();
        boolean speedChanged = false;
        
        // Check speed effect expiration
        if (hasSpeedEffect && currentTime >= speedEffectEndTime) {
            hasSpeedEffect = false;
            speedChanged = true;
        }
        
        // Check slow effect expiration
        if (hasSlowEffect && currentTime >= slowEffectEndTime) {
            hasSlowEffect = false;
            speedChanged = true;
        }
        
        // Update game speed based on active effects
        if (speedChanged) {
            if (hasSpeedEffect && !hasSlowEffect) {
                currentDelay = Math.max(MIN_DELAY, originalDelay / 2);
            } else if (hasSlowEffect && !hasSpeedEffect) {
                currentDelay = originalDelay * 2;
            } else if (!hasSpeedEffect && !hasSlowEffect) {
                currentDelay = originalDelay;
            }
            // If both effects are active, they cancel each other out (use original delay)
            timer.setDelay(currentDelay);
        }
    }

    /**
     * Main rendering method - paints all game elements.
     * Uses Graphics2D for better rendering quality with antialiasing.
     * Renders different screens based on game state (welcome, playing, paused, game over).
     * Applies screen shake effects when active.
     * 
     * @param g The Graphics context for painting
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Apply screen shake effect if active
        Point shakeOffset = animationManager.getScreenShakeOffset();
        if (shakeOffset.x != 0 || shakeOffset.y != 0) {
            g2d.translate(shakeOffset.x, shakeOffset.y);
        }

        // Draw background elements
        drawGrid(g2d);
        drawWalls(g2d);
        drawObstacles(g2d);

        if (running) {
            drawGame(g2d);

            // Draw overlay screens based on game state
            if (!gameStarted) {
                drawWelcomeScreen(g2d);
            } else if (paused) {
                drawPausedScreen(g2d);
            }
        } else {
            drawGameOver(g2d);
        }
        
        // Reset transformation if shake was applied
        if (shakeOffset.x != 0 || shakeOffset.y != 0) {
            g2d.translate(-shakeOffset.x, -shakeOffset.y);
        }
    }

    /** Timestamp of the last direction change to prevent rapid direction changes */
    private long lastDirectionChange = 0;

    /**
     * Draws the welcome screen overlay with game title and instructions.
     * Shows the current theme name and basic controls to start the game.
     * 
     * @param g The Graphics2D context for drawing
     */
    private void drawWelcomeScreen(Graphics2D g) {
        // Semi-transparent overlay
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, getWidth(), getHeight());

        // Game title with theme name
        g.setColor(currentTheme.textColor);
        g.setFont(new Font("SansSerif", Font.BOLD, 30));
        String msg = "Snake Game - " + currentTheme.name + " Theme";
        int msgWidth = g.getFontMetrics().stringWidth(msg);
        g.drawString(msg, (getWidth() - msgWidth) / 2, getHeight() / 2 - 60);

        // Start instructions
        g.setFont(new Font("SansSerif", Font.PLAIN, 18));
        String startMsg = "Press Spacebar or R to start game";
        int startMsgWidth = g.getFontMetrics().stringWidth(startMsg);
        g.drawString(startMsg, (getWidth() - startMsgWidth) / 2, getHeight() / 2);

        // Help instructions
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        String helpMsg = "Click \"?\" button in bottom right or press Shift+/ for instructions";
        int helpMsgWidth = g.getFontMetrics().stringWidth(helpMsg);
        g.drawString(helpMsg, (getWidth() - helpMsgWidth) / 2, getHeight() / 2 + 40);
    }

    /**
     * Draws the pause screen overlay.
     * Shows a semi-transparent overlay with pause message and resume instructions.
     * 
     * @param g The Graphics2D context for drawing
     */
    private void drawPausedScreen(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(currentTheme.textColor);
        g.setFont(new Font("SansSerif", Font.BOLD, 30));
        String msg = "Paused";
        int msgWidth = g.getFontMetrics().stringWidth(msg);
        g.drawString(msg, (getWidth() - msgWidth) / 2, getHeight() / 2);
        g.setFont(new Font("SansSerif", Font.PLAIN, 16));
        String subMsg = "Press Spacebar to continue";
        int subMsgWidth = g.getFontMetrics().stringWidth(subMsg);
        g.drawString(subMsg, (getWidth() - subMsgWidth) / 2, getHeight() / 2 + 30);
    }

    /**
     * Draws the game grid lines.
     * Creates a visual grid to help players see cell boundaries.
     * 
     * @param g The Graphics2D context for drawing
     */
    private void drawGrid(Graphics2D g) {
        g.setColor(currentTheme.gridColor);
        // Draw vertical lines
        for (int i = 0; i < selectedMap.width; i++) {
            g.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, selectedMap.height * CELL_SIZE);
        }
        // Draw horizontal lines
        for (int i = 0; i < selectedMap.height; i++) {
            g.drawLine(0, i * CELL_SIZE, selectedMap.width * CELL_SIZE, i * CELL_SIZE);
        }
    }

    /**
     * Draws the boundary walls around the game area.
     * Creates thin wall borders that the snake cannot pass through.
     * 
     * @param g The Graphics2D context for drawing
     */
    private void drawWalls(Graphics2D g) {
        g.setColor(currentTheme.wallColor);
        // Top wall
        g.fillRect(0, 0, selectedMap.width * CELL_SIZE, CELL_SIZE / 4);
        // Bottom wall
        g.fillRect(0, selectedMap.height * CELL_SIZE - CELL_SIZE / 4, selectedMap.width * CELL_SIZE, CELL_SIZE / 4);
        // Left wall
        g.fillRect(0, 0, CELL_SIZE / 4, selectedMap.height * CELL_SIZE);
        // Right wall
        g.fillRect(selectedMap.width * CELL_SIZE - CELL_SIZE / 4, 0, CELL_SIZE / 4, selectedMap.height * CELL_SIZE);
    }

    /**
     * Draws all obstacles on the map.
     * Obstacles are solid blocks that the snake cannot pass through.
     * 
     * @param g The Graphics2D context for drawing
     */
    private void drawObstacles(Graphics2D g) {
        g.setColor(currentTheme.obstacleColor);
        for (Point p : selectedMap.obstacles) {
            g.fillRect(p.x * CELL_SIZE, p.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
    }

    /**
     * Draws all game elements during active gameplay.
     * Renders the snake, food, score information, lives, and control instructions.
     * 
     * @param g The Graphics2D context for drawing
     */
    private void drawGame(Graphics2D g) {
        // Draw snake with different colors for head and body
        for (int i = 0; i < snake.size(); i++) {
            if (i == 0) {
                g.setColor(currentTheme.snakeColor.brighter()); // Brighter head
            } else {
                g.setColor(currentTheme.snakeColor); // Regular body
            }
            Point p = snake.get(i);
            g.fillRoundRect(p.x * CELL_SIZE + 1, p.y * CELL_SIZE + 1,
                    CELL_SIZE - 2, CELL_SIZE - 2, 8, 8);
        }

        // Draw food with type-specific appearance and animations
        drawFood(g);

        // Draw game information (score, level, theme)
        g.setColor(currentTheme.textColor);
        g.setFont(gameFont);
        g.drawString("Score: " + score, 10, 20);
        g.drawString("High Score: " + highScore, 120, 20);
        g.drawString("Level: " + level, 240, 20);
        g.drawString("Theme: " + currentTheme.name, 340, 20);
        
        // Draw sound status indicator
        String soundStatus = soundManager.isSoundEnabled() ? "♪ Sound: ON" : "♪ Sound: OFF";
        g.drawString(soundStatus, 480, 20);
        
        // Draw active food effects
        drawActiveEffects(g);

        // Draw lives as red circles
        drawLivesIndicator(g);

        drawInstructions(g);
    }

    /**
     * Draws the food with type-specific appearance and animations.
     * 
     * @param g The Graphics2D context for drawing
     */
    private void drawFood(Graphics2D g) {
        if (food == null || currentFoodType == null) return;
        
        // Get animation values
        float scale = animationManager.getFoodScale();
        float rotation = animationManager.getFoodRotation();
        
        // Calculate food position and size with animation
        int baseX = food.x * CELL_SIZE;
        int baseY = food.y * CELL_SIZE;
        int baseSize = CELL_SIZE - 4;
        
        int animatedSize = (int) (baseSize * scale);
        int offsetX = (baseSize - animatedSize) / 2;
        int offsetY = (baseSize - animatedSize) / 2;
        
        // Save graphics state
        Graphics2D g2d = (Graphics2D) g.create();
        
        // Apply rotation if needed
        if (rotation != 0) {
            int centerX = baseX + CELL_SIZE / 2;
            int centerY = baseY + CELL_SIZE / 2;
            g2d.rotate(Math.toRadians(rotation), centerX, centerY);
        }
        
        // Get food color based on type and theme
        Color foodColor = currentFoodType.getThemeColor(currentTheme);
        g2d.setColor(foodColor);
        
        // Draw food based on type
        switch (currentFoodType) {
            case NORMAL:
                g2d.fillOval(baseX + 2 + offsetX, baseY + 2 + offsetY, animatedSize, animatedSize);
                break;
                
            case BONUS:
                // Draw as a star shape
                drawStar(g2d, baseX + CELL_SIZE / 2, baseY + CELL_SIZE / 2, animatedSize / 2, foodColor);
                break;
                
            case SPEED:
                // Draw as a diamond with lightning effect
                drawDiamond(g2d, baseX + CELL_SIZE / 2, baseY + CELL_SIZE / 2, animatedSize / 2, foodColor);
                // Add lightning effect
                g2d.setColor(Color.YELLOW);
                g2d.setStroke(new BasicStroke(2));
                int lightningX = baseX + CELL_SIZE / 2;
                int lightningY = baseY + CELL_SIZE / 2;
                g2d.drawLine(lightningX - 3, lightningY - 6, lightningX + 3, lightningY + 6);
                g2d.drawLine(lightningX - 6, lightningY, lightningX + 6, lightningY);
                break;
                
            case SLOW:
                // Draw as a hexagon
                drawHexagon(g2d, baseX + CELL_SIZE / 2, baseY + CELL_SIZE / 2, animatedSize / 2, foodColor);
                break;
                
            case MEGA:
                // Draw as a large circle with pulsing effect
                g2d.fillOval(baseX + 2 + offsetX, baseY + 2 + offsetY, animatedSize, animatedSize);
                // Add outer ring
                g2d.setColor(foodColor.brighter());
                g2d.setStroke(new BasicStroke(3));
                g2d.drawOval(baseX + 2 + offsetX, baseY + 2 + offsetY, animatedSize, animatedSize);
                break;
                
            case SHRINK:
                // Draw as a small triangle
                drawTriangle(g2d, baseX + CELL_SIZE / 2, baseY + CELL_SIZE / 2, animatedSize / 2, foodColor);
                break;
        }
        
        // Draw food type indicator text
        if (!animationManager.isFoodAnimationActive()) {
            g2d.setColor(currentTheme.textColor);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 8));
            String typeText = currentFoodType.name().substring(0, 1);
            FontMetrics fm = g2d.getFontMetrics();
            int textX = baseX + (CELL_SIZE - fm.stringWidth(typeText)) / 2;
            int textY = baseY + CELL_SIZE + 12;
            g2d.drawString(typeText, textX, textY);
        }
        
        g2d.dispose();
    }
    
    /**
     * Draws a star shape for bonus food.
     */
    private void drawStar(Graphics2D g, int centerX, int centerY, int radius, Color color) {
        int[] xPoints = new int[10];
        int[] yPoints = new int[10];
        
        for (int i = 0; i < 10; i++) {
            double angle = Math.PI * i / 5.0;
            int r = (i % 2 == 0) ? radius : radius / 2;
            xPoints[i] = centerX + (int) (r * Math.cos(angle - Math.PI / 2));
            yPoints[i] = centerY + (int) (r * Math.sin(angle - Math.PI / 2));
        }
        
        g.setColor(color);
        g.fillPolygon(xPoints, yPoints, 10);
    }
    
    /**
     * Draws a diamond shape for speed food.
     */
    private void drawDiamond(Graphics2D g, int centerX, int centerY, int radius, Color color) {
        int[] xPoints = {centerX, centerX + radius, centerX, centerX - radius};
        int[] yPoints = {centerY - radius, centerY, centerY + radius, centerY};
        
        g.setColor(color);
        g.fillPolygon(xPoints, yPoints, 4);
    }
    
    /**
     * Draws a hexagon shape for slow food.
     */
    private void drawHexagon(Graphics2D g, int centerX, int centerY, int radius, Color color) {
        int[] xPoints = new int[6];
        int[] yPoints = new int[6];
        
        for (int i = 0; i < 6; i++) {
            double angle = Math.PI * i / 3.0;
            xPoints[i] = centerX + (int) (radius * Math.cos(angle));
            yPoints[i] = centerY + (int) (radius * Math.sin(angle));
        }
        
        g.setColor(color);
        g.fillPolygon(xPoints, yPoints, 6);
    }
    
    /**
     * Draws a triangle shape for shrink food.
     */
    private void drawTriangle(Graphics2D g, int centerX, int centerY, int radius, Color color) {
        int[] xPoints = {centerX, centerX + radius, centerX - radius};
        int[] yPoints = {centerY - radius, centerY + radius, centerY + radius};
        
        g.setColor(color);
        g.fillPolygon(xPoints, yPoints, 3);
    }
    
    /**
     * Draws active food effects indicators.
     * 
     * @param g The Graphics2D context for drawing
     */
    private void drawActiveEffects(Graphics2D g) {
        int y = 40;
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        
        if (hasSpeedEffect) {
            long remaining = (speedEffectEndTime - System.currentTimeMillis()) / 1000;
            g.setColor(Color.YELLOW);
            g.drawString("⚡ SPEED: " + remaining + "s", 10, y);
            y += 15;
        }
        
        if (hasSlowEffect) {
            long remaining = (slowEffectEndTime - System.currentTimeMillis()) / 1000;
            g.setColor(Color.BLUE);
            g.drawString("🐌 SLOW: " + remaining + "s", 10, y);
            y += 15;
        }
        
        // Show current food type
        if (currentFoodType != null && currentFoodType != FoodType.NORMAL) {
            g.setColor(currentFoodType.getThemeColor(currentTheme));
            g.drawString("Next: " + currentFoodType.getDescription(), 10, y);
        }
    }
    
    /**
     * Draws the lives indicator using red circles.
     */
    private void drawLivesIndicator(Graphics2D g) {
        g.setColor(Color.RED);
        int startX = selectedMap.width * CELL_SIZE - LIFE_INDICATOR_SPACING;
        for (int i = 0; i < lives; i++) {
            g.fillOval(startX - (i * LIFE_INDICATOR_SPACING), 10, 
                      LIFE_INDICATOR_SIZE, LIFE_INDICATOR_SIZE);
        }
    }
    
    /**
     * Draws control instructions at the bottom of the screen.
     * Shows basic game controls and food types for player reference.
     * 
     * @param g The Graphics2D context for drawing
     */
    private void drawInstructions(Graphics2D g) {
        g.setColor(currentTheme.textColor);
        g.setFont(instructionFont);
        int y = getHeight() - 50; // Move closer to bottom edge
        String[] instructions = {
            "Controls: Arrow keys/WASD: Move | Spacebar/P: Pause | M: Sound | R: Restart",
            "Food Types: ● Normal (10pts) | ⭐ Bonus (25pts) | ⚡ Speed | 🐌 Slow | ⬢ Mega (50pts) | ▲ Shrink"
        };
        
        for (int i = 0; i < instructions.length; i++) {
            g.drawString(instructions[i], 10, y + (i * 15));
        }
    }

    /**
     * Draws the game over screen with final statistics.
     * Shows final score, high score, level achieved, and restart instructions.
     * 
     * @param g The Graphics2D context for drawing
     */
    private void drawGameOver(Graphics2D g) {
        g.setColor(currentTheme.textColor);
        
        // Game over title
        g.setFont(new Font("SansSerif", Font.BOLD, 30));
        String msg = "Game Over";
        int msgWidth = g.getFontMetrics().stringWidth(msg);
        g.drawString(msg, (getWidth() - msgWidth) / 2, getHeight() / 2 - 60);

        // Final statistics
        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        String scoreMsg = "Final Score: " + score;
        int scoreWidth = g.getFontMetrics().stringWidth(scoreMsg);
        g.drawString(scoreMsg, (getWidth() - scoreWidth) / 2, getHeight() / 2 - 25);

        String highScoreMsg = "High Score: " + highScore;
        int highScoreWidth = g.getFontMetrics().stringWidth(highScoreMsg);
        g.drawString(highScoreMsg, (getWidth() - highScoreWidth) / 2, getHeight() / 2 + 5);

        String levelMsg = "Final Level: " + level;
        int levelWidth = g.getFontMetrics().stringWidth(levelMsg);
        g.drawString(levelMsg, (getWidth() - levelWidth) / 2, getHeight() / 2 + 35);

        // Restart instructions
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        String restartMsg = "Press R to restart game";
        int restartWidth = g.getFontMetrics().stringWidth(restartMsg);
        g.drawString(restartMsg, (getWidth() - restartWidth) / 2, getHeight() / 2 + 70);
    }

    /**
     * Inner class that handles keyboard input for the Snake game.
     * Manages game controls including movement, pause/resume, and restart functionality.
     * 
     * Key Controls:
     * - Arrow keys or WASD: Control snake movement
     * - Spacebar or P: Pause/resume the game
     * - R: Restart the game
     * 
     * Movement Logic:
     * - Prevents the snake from immediately reversing direction (e.g., can't go left if moving right)
     * - Uses safe direction change checking to prevent immediate collisions
     * - Only processes movement input when the game is in PLAYING state
     */
    class GameKeyAdapter extends KeyAdapter {
        /**
         * Handles key press events for game control.
         * Processes pause/resume, restart, and movement commands.
         * 
         * @param e The KeyEvent containing information about the pressed key
         */
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_SLASH && e.isShiftDown()) {
                showInstructionDialog();
                return;
            }

            if (running) {
                if (!gameStarted) {
                    if (key == KeyEvent.VK_SPACE || key == KeyEvent.VK_R) {
                        gameStarted = true;
                        paused = false;
                    }
                    return;
                }

                if (key == KeyEvent.VK_P || key == KeyEvent.VK_SPACE) {
                    paused = !paused;
                    return;
                }

                if (paused) return;

                // Store the current direction to prevent multiple changes in one frame
                int newDirection = direction;
                
                if ((key == KeyEvent.VK_UP || key == KeyEvent.VK_W) && direction != KeyEvent.VK_DOWN) {
                    newDirection = KeyEvent.VK_UP;
                } else if ((key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) && direction != KeyEvent.VK_UP) {
                    newDirection = KeyEvent.VK_DOWN;
                } else if ((key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) && direction != KeyEvent.VK_RIGHT) {
                    newDirection = KeyEvent.VK_LEFT;
                } else if ((key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) && direction != KeyEvent.VK_LEFT) {
                    newDirection = KeyEvent.VK_RIGHT;
                }
                
                // Only update direction if it's different and safe
                if (newDirection != direction && isSafeDirectionChange(newDirection)) {
                    direction = newDirection;
                }
            } else if (key == KeyEvent.VK_R) {
                initGame();
            } else if (key == KeyEvent.VK_M) {
                // Toggle sound on/off
                soundManager.setSoundEnabled(!soundManager.isSoundEnabled());
                if (soundManager.isSoundEnabled() && running && !paused) {
                    soundManager.playBackgroundMusic();
                } else {
                    soundManager.stopBackgroundMusic();
                }
            } else if (key == KeyEvent.VK_ESCAPE) {
                // Return to map selection
                if (timer != null) timer.stop();
                soundManager.stopBackgroundMusic();
                parent.showMapSelectPanel();
            }
        }
    }
}


