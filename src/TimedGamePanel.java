import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
 * A time-limited version of the Snake game with a 60-second countdown.
 * 
 * This game mode adds urgency and excitement by challenging players to
 * achieve the highest score possible within a fixed time limit. The game
 * features:
 * - 60-second countdown timer
 * - Score tracking with time pressure
 * - Automatic game over when time expires
 * - Same snake mechanics as classic mode
 * 
 * Players must balance speed and safety as they race against time to
 * collect food and grow their snake. The time constraint creates a unique
 * strategic challenge different from the endless classic mode.
 * 
 * Controls:
 * - Arrow keys or WASD for movement
 * - Game automatically ends when timer reaches zero
 * 
 * @author Snake Game Development Team
 * @version 1.0
 */
public class TimedGamePanel extends JPanel implements ActionListener {
    /** Reference to the main game window for navigation */
    private SnakeGame parent;

    /** Timer that controls game loop and snake movement */
    private javax.swing.Timer timer;
    
    /** List of points representing the snake's body segments */
    private ArrayList<Point> snake;
    
    /** Current movement direction (KeyEvent constants) */
    private int direction;
    
    /** Whether the game is currently active */
    private boolean running;
    
    /** Current player score (number of food items eaten) */
    private int score;
    
    /** Current position of the food on the game board */
    private Point food;
    
    /** Current type of food with special effects */
    private FoodType currentFoodType;
    
    /** Random number generator for food placement */
    private Random random;
    
    /** Remaining time in seconds (starts at 60) */
    private int timeLeft = 60;
    
    /** Size of each cell in pixels */
    private static final int CELL_SIZE = 20;
    
    /** Game board dimensions in cells */
    private static final int WIDTH = 25, HEIGHT = 25;
    
    /** Initial delay between game updates in milliseconds */
    private static final int INITIAL_DELAY = 120;
    
    /** Font used for rendering game text */
    private Font gameFont = new Font("SansSerif", Font.BOLD, 16);
    
    /** Flag to prevent multiple game over dialogs */
    private boolean gameOverDialogShown = false;
    
    /** Current visual theme for colors and styling */
    private Theme currentTheme;

    /**
     * Creates a new timed game panel with the specified parent window.
     * 
     * Initializes the game board, sets up the UI components, and starts
     * the game immediately. The panel is configured with a dark background
     * and proper focus handling for keyboard input.
     * 
     * @param parent The main game window that contains this panel
     */
    public TimedGamePanel(SnakeGame parent) {
        this.parent = parent;
        this.currentTheme = Settings.getInstance().getCurrentTheme();
        setPreferredSize(new Dimension(WIDTH * CELL_SIZE, HEIGHT * CELL_SIZE));
        setBackground(currentTheme.backgroundColor);
        setFocusable(true);
        setLayout(null);
        random = new Random();
        initGame();
        addKeyListener(new TimedKeyAdapter());
        requestFocusInWindow();
    }

    private void initGame() {
        snake = new ArrayList<>();
        snake.add(new Point(WIDTH / 2, HEIGHT / 2));
        snake.add(new Point(WIDTH / 2, HEIGHT / 2 + 1));
        snake.add(new Point(WIDTH / 2, HEIGHT / 2 + 2));
        direction = KeyEvent.VK_UP;
        running = true;
        score = 0;
        timeLeft = 60;
        gameOverDialogShown = false;
        currentFoodType = FoodType.NORMAL;
        generateFood();
        if (timer != null) timer.stop();
        timer = new javax.swing.Timer(INITIAL_DELAY, this);
        timer.start();
        // Countdown thread
        new Thread(() -> {
            while (running && timeLeft > 0) {
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                timeLeft--;
                repaint();
                if (timeLeft == 0) {
                    running = false;
                    timer.stop();

                    SwingUtilities.invokeLater(this::showGameOverDialog);
                }
            }
        }).start();
    }

    private void generateFood() {
        int x, y;
        do {
            x = random.nextInt(WIDTH);
            y = random.nextInt(HEIGHT);
        } while (isOnSnake(x, y));
        food = new Point(x, y);
        // Generate random food type for enhanced gameplay
        currentFoodType = FoodType.getRandomFoodType();
    }

    private boolean isOnSnake(int x, int y) {
        for (Point p : snake) if (p.x == x && p.y == y) return true;
        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) move();
        repaint();
    }

    private void move() {
        Point head = snake.get(0);
        Point newHead = new Point(head);
        switch (direction) {
            case KeyEvent.VK_UP: newHead.y--; break;
            case KeyEvent.VK_DOWN: newHead.y++; break;
            case KeyEvent.VK_LEFT: newHead.x--; break;
            case KeyEvent.VK_RIGHT: newHead.x++; break;
        }
        if (newHead.x < 0 || newHead.x >= WIDTH || newHead.y < 0 || newHead.y >= HEIGHT) {
            running = false;
            timer.stop();
            showGameOverDialog();
            return;
        }
        for (int i = 1; i < snake.size(); i++) {
            if (snake.get(i).equals(newHead)) {
                running = false;
                timer.stop();
                showGameOverDialog();
                return;
            }
        }
        snake.add(0, newHead);
        if (newHead.equals(food)) {
            score += 10;
            generateFood();
        } else {
            snake.remove(snake.size() - 1);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw background elements
        drawGrid(g2d);
        drawWalls(g2d);
        
        if (running) {
            drawGame(g2d);
        } else {
            drawGameOver(g2d);
        }
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
        for (int i = 0; i <= WIDTH; i++) {
            g.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, HEIGHT * CELL_SIZE);
        }
        // Draw horizontal lines
        for (int i = 0; i <= HEIGHT; i++) {
            g.drawLine(0, i * CELL_SIZE, WIDTH * CELL_SIZE, i * CELL_SIZE);
        }
    }
    
    /**
     * Draws the border walls around the game area.
     * 
     * @param g The Graphics2D context for drawing
     */
    private void drawWalls(Graphics2D g) {
        g.setColor(currentTheme.wallColor);
        // Draw border walls
        g.fillRect(0, 0, WIDTH * CELL_SIZE, CELL_SIZE / 4);
        g.fillRect(0, HEIGHT * CELL_SIZE - CELL_SIZE / 4, WIDTH * CELL_SIZE, CELL_SIZE / 4);
        g.fillRect(0, 0, CELL_SIZE / 4, HEIGHT * CELL_SIZE);
        g.fillRect(WIDTH * CELL_SIZE - CELL_SIZE / 4, 0, CELL_SIZE / 4, HEIGHT * CELL_SIZE);
    }
    
    /**
     * Draws the main game elements including snake, food, and UI.
     * 
     * @param g The Graphics2D context for drawing
     */
    private void drawGame(Graphics2D g) {
        // Draw snake with head distinction
        for (int i = 0; i < snake.size(); i++) {
            if (i == 0) {
                g.setColor(currentTheme.snakeColor.brighter()); // Head is brighter
            } else {
                g.setColor(currentTheme.snakeColor); // Body
            }
            Point p = snake.get(i);
            g.fillRoundRect(p.x * CELL_SIZE + 1, p.y * CELL_SIZE + 1, CELL_SIZE - 2, CELL_SIZE - 2, 8, 8);
        }
        
        // Draw food based on type
        int foodX = food.x * CELL_SIZE + 2;
        int foodY = food.y * CELL_SIZE + 2;
        int foodSize = CELL_SIZE - 4;
        
        switch (currentFoodType) {
            case NORMAL:
                g.setColor(currentTheme.foodColor);
                g.fillOval(foodX, foodY, foodSize, foodSize);
                break;
            case BONUS:
                g.setColor(Color.YELLOW);
                // Draw star shape
                int[] xPoints = {foodX + foodSize/2, foodX + foodSize*3/5, foodX + foodSize, foodX + foodSize*2/3, foodX + foodSize*4/5, foodX + foodSize/2, foodX + foodSize/5, foodX + foodSize/3, foodX, foodX + foodSize*2/5};
                int[] yPoints = {foodY, foodY + foodSize*2/5, foodY + foodSize*2/5, foodY + foodSize*3/5, foodY + foodSize, foodY + foodSize*4/5, foodY + foodSize, foodY + foodSize*3/5, foodY + foodSize*2/5, foodY + foodSize*2/5};
                g.fillPolygon(xPoints, yPoints, 10);
                break;
            case SPEED:
                g.setColor(Color.CYAN);
                // Draw lightning bolt
                int[] xLightning = {foodX + foodSize/3, foodX + foodSize*2/3, foodX + foodSize/2, foodX + foodSize*2/3, foodX + foodSize/3, foodX + foodSize/2};
                int[] yLightning = {foodY, foodY + foodSize/3, foodY + foodSize/2, foodY + foodSize, foodY + foodSize*2/3, foodY + foodSize/2};
                g.fillPolygon(xLightning, yLightning, 6);
                break;
            case SLOW:
                g.setColor(Color.ORANGE);
                // Draw spiral
                for (int i = 0; i < 3; i++) {
                    g.fillOval(foodX + i*2, foodY + i*2, foodSize - i*4, foodSize - i*4);
                }
                break;
            case MEGA:
                g.setColor(Color.MAGENTA);
                // Draw hexagon
                int[] xHex = {foodX + foodSize/4, foodX + foodSize*3/4, foodX + foodSize, foodX + foodSize*3/4, foodX + foodSize/4, foodX};
                int[] yHex = {foodY, foodY, foodY + foodSize/2, foodY + foodSize, foodY + foodSize, foodY + foodSize/2};
                g.fillPolygon(xHex, yHex, 6);
                break;
        }
        
        // Draw UI elements
        drawUI(g);
    }
    
    /**
     * Draws the user interface elements including score, timer, and instructions.
     * 
     * @param g The Graphics2D context for drawing
     */
    private void drawUI(Graphics2D g) {
        // Score display with transparency
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
        g.setColor(currentTheme.snakeColor);
        g.fillRoundRect(10, 10, 130, 32, 16, 16);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g.setColor(currentTheme.textColor);
        g.drawString("Score: " + score, 22, 32);
        
        // Timer display with transparency
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
        if (timeLeft > 10) {
            g.setColor(currentTheme.snakeColor.darker()); // Normal color
        } else {
            g.setColor(Color.RED); // Red for urgency when time is low
        }
        g.fillRoundRect(150, 10, 140, 32, 16, 16);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g.setColor(currentTheme.textColor);
        g.drawString("Time: " + timeLeft + "s", 162, 32);
        
        // Control instructions - positioned at bottom left corner
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.setColor(currentTheme.textColor.darker());
        g.drawString("Controls: Arrow keys/WASD: Move | R: Restart | ESC: Exit", 10, getHeight() - 30);
        
        // Food type legend - positioned above controls in bottom left
        if (currentFoodType != null) {
            g.drawString("Current Food: " + currentFoodType.name + " (" + currentFoodType.points + " pts)", 10, getHeight() - 50);
        }
    }
    
    /**
     * Draws the game over screen with final score.
     * 
     * @param g The Graphics2D context for drawing
     */
    private void drawGameOver(Graphics2D g) {
        // Semi-transparent overlay
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, getWidth(), getHeight());
        
        // Game Over title
        g.setColor(currentTheme.textColor);
        g.setFont(new Font("SansSerif", Font.BOLD, 36));
        String msg = timeLeft == 0 ? "Time's Up!" : "Game Over";
        int msgWidth = g.getFontMetrics().stringWidth(msg);
        g.drawString(msg, (getWidth() - msgWidth) / 2, getHeight() / 2 - 40);
        
        // Final score
        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        String scoreMsg = "Final Score: " + score;
        int scoreMsgWidth = g.getFontMetrics().stringWidth(scoreMsg);
        g.drawString(scoreMsg, (getWidth() - scoreMsgWidth) / 2, getHeight() / 2);
        
        // Restart instructions
        g.setFont(new Font("SansSerif", Font.PLAIN, 16));
        String restartMsg = "Press R to restart or ESC to return to menu";
        int restartMsgWidth = g.getFontMetrics().stringWidth(restartMsg);
        g.drawString(restartMsg, (getWidth() - restartMsgWidth) / 2, getHeight() / 2 + 40);
    }

    private void showGameOverDialog() {
        if (gameOverDialogShown) return;
        gameOverDialogShown = true;
        
        JDialog gameOverDialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Timed Mode - Game Over", true);
        gameOverDialog.setSize(420, 320);
        gameOverDialog.setLocationRelativeTo(this);
        gameOverDialog.setResizable(false);
        
        // Create main panel with theme colors
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(currentTheme.backgroundColor);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(currentTheme.wallColor, 2),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(currentTheme.backgroundColor);
        String titleText = timeLeft == 0 ? "⏰ Time's Up!" : "🎮 Game Over";
        JLabel titleLabel = new JLabel(titleText, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(currentTheme.textColor);
        titlePanel.add(titleLabel);
        
        // Score panel
        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.Y_AXIS));
        scorePanel.setBackground(currentTheme.backgroundColor);
        scorePanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));
        
        // Final score with attractive styling
        JLabel scoreLabel = new JLabel("🏆 Final Score: " + score, JLabel.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        scoreLabel.setForeground(currentTheme.snakeColor.brighter());
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Time information
        String timeText = timeLeft == 0 ? "⏱️ Time Expired" : "⏱️ Time Remaining: " + timeLeft + "s";
        JLabel timeLabel = new JLabel(timeText, JLabel.CENTER);
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        timeLabel.setForeground(currentTheme.foodColor);
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Performance message
        String performanceText;
        Color performanceColor;
        if (score >= 200) {
            performanceText = "🌟 Excellent Performance!";
            performanceColor = currentTheme.snakeColor.brighter();
        } else if (score >= 100) {
            performanceText = "👍 Good Job!";
            performanceColor = currentTheme.foodColor.brighter();
        } else if (score >= 50) {
            performanceText = "👌 Not Bad!";
            performanceColor = currentTheme.textColor;
        } else {
            performanceText = "💪 Keep Practicing!";
            performanceColor = currentTheme.gridColor.brighter();
        }
        
        JLabel performanceLabel = new JLabel(performanceText, JLabel.CENTER);
        performanceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        performanceLabel.setForeground(performanceColor);
        performanceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        scorePanel.add(scoreLabel);
        scorePanel.add(Box.createVerticalStrut(12));
        scorePanel.add(timeLabel);
        scorePanel.add(Box.createVerticalStrut(15));
        scorePanel.add(performanceLabel);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(currentTheme.backgroundColor);
        
        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Arial", Font.BOLD, 14));
        okButton.setBackground(currentTheme.wallColor);
        okButton.setForeground(currentTheme.textColor);
        okButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(currentTheme.gridColor, 1),
            BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        okButton.setFocusPainted(false);
        okButton.addActionListener(e -> gameOverDialog.dispose());
        
        // Add hover effect
        okButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                okButton.setBackground(currentTheme.wallColor.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                okButton.setBackground(currentTheme.wallColor);
            }
        });
        
        buttonPanel.add(okButton);
        
        // Assemble dialog
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(scorePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        gameOverDialog.add(mainPanel);
        gameOverDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        gameOverDialog.setVisible(true);
    }

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
     * Updates the current theme and refreshes the panel appearance.
     * Called when theme settings are changed.
     */
    public void updateTheme() {
        currentTheme = Settings.getInstance().getCurrentTheme();
        setBackground(currentTheme.backgroundColor);
        repaint();
    }

    private class TimedKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (running) {
                // Store the current direction to prevent multiple changes in one frame
                int newDirection = direction;
                
                if ((key == KeyEvent.VK_UP) && direction != KeyEvent.VK_DOWN) newDirection = KeyEvent.VK_UP;
                else if ((key == KeyEvent.VK_DOWN) && direction != KeyEvent.VK_UP) newDirection = KeyEvent.VK_DOWN;
                else if ((key == KeyEvent.VK_LEFT) && direction != KeyEvent.VK_RIGHT) newDirection = KeyEvent.VK_LEFT;
                else if ((key == KeyEvent.VK_RIGHT) && direction != KeyEvent.VK_LEFT) newDirection = KeyEvent.VK_RIGHT;
                else if ((key == KeyEvent.VK_W) && direction != KeyEvent.VK_DOWN) newDirection = KeyEvent.VK_UP;
                else if ((key == KeyEvent.VK_S) && direction != KeyEvent.VK_UP) newDirection = KeyEvent.VK_DOWN;
                else if ((key == KeyEvent.VK_A) && direction != KeyEvent.VK_RIGHT) newDirection = KeyEvent.VK_LEFT;
                else if ((key == KeyEvent.VK_D) && direction != KeyEvent.VK_LEFT) newDirection = KeyEvent.VK_RIGHT;
                
                // Only update direction if it's different and safe
                if (newDirection != direction && isSafeDirectionChange(newDirection)) {
                    direction = newDirection;
                }
            }
            if (key == KeyEvent.VK_R && !running) {
                initGame();
            }
            if (key == KeyEvent.VK_ESCAPE) {
                parent.showMapSelectPanel();
            }
        }
    }
}