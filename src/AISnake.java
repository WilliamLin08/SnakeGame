import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

/**
 * AI-controlled snake for the VS AI game mode.
 * 
 * This class implements an intelligent snake that can play against human players
 * with three different difficulty levels:
 * - Easy (1): Makes random moves 70% of the time, occasionally tracks food
 * - Medium (2): Tracks food 80% of the time, with some random movement
 * - Hard (3): Intelligently tracks food while avoiding obstacles and collisions
 * 
 * The AI uses pathfinding algorithms and collision detection to make strategic
 * decisions about movement direction.
 * 
 * @author Snake Game Development Team
 * @version 1.0
 */
public class AISnake {
    /** The body segments of the AI snake, with head at index 0 */
    private ArrayList<Point> snake;
    
    /** Current position of the food on the game board */
    private Point food;
    
    /** Current movement direction using KeyEvent constants (UP, DOWN, LEFT, RIGHT) */
    private int direction;
    
    /** Random number generator for making probabilistic movement decisions */
    private Random random;
    
    /** Width of the game board in grid cells */
    private int width;
    
    /** Height of the game board in grid cells */
    private int height;
    
    /** List of obstacle positions that the AI must avoid */
    private ArrayList<Point> obstacles;
    
    /** Reference to the human player's snake for collision avoidance */
    private ArrayList<Point> playerSnake;
    
    /** AI difficulty level: 1 = Easy, 2 = Medium, 3 = Hard */
    private int difficulty;
    
    /** The previous movement direction to prevent immediate reversals */
    private int lastDirection;
    
    /** Counter for consecutive moves in the same direction */
    private int consecutiveMoves;
    
    /** Maximum allowed consecutive moves before forcing direction change */
    private static final int MAX_CONSECUTIVE_MOVES = 3;

    /**
     * Creates a new AI snake with specified game parameters.
     * 
     * @param width The width of the game board in grid cells
     * @param height The height of the game board in grid cells
     * @param obstacles List of obstacle positions that the AI must navigate around
     * @param difficulty The AI difficulty level (1=Easy, 2=Medium, 3=Hard)
     */
    public AISnake(int width, int height, ArrayList<Point> obstacles, int difficulty) {
        this.width = width;
        this.height = height;
        this.obstacles = obstacles;
        this.random = new Random();
        this.snake = new ArrayList<>();
        this.difficulty = difficulty;
        this.lastDirection = KeyEvent.VK_DOWN;
        this.consecutiveMoves = 0;
        resetSnake();
    }

    /**
     * Resets the AI snake to its initial state.
     * 
     * Clears the current snake body and creates a new 3-segment snake
     * positioned in the top-left area of the game board, moving downward.
     * This method is called when starting a new game or after game over.
     */
    public void resetSnake() {
        snake.clear();
        // Generate AI snake in the top-left corner to avoid conflicts with player
        snake.add(new Point(2, 2));  // Head
        snake.add(new Point(2, 3));  // Body segment 1
        snake.add(new Point(2, 4));  // Body segment 2
        direction = KeyEvent.VK_DOWN;
    }

    /**
     * Updates the current food position for AI pathfinding.
     * 
     * @param food The new position of the food on the game board
     */
    public void setFood(Point food) {
        this.food = food;
    }

    /**
     * Updates the reference to the player's snake for collision avoidance.
     * 
     * @param playerSnake The current body segments of the human player's snake
     */
    public void setPlayerSnake(ArrayList<Point> playerSnake) {
        this.playerSnake = playerSnake;
    }

    /**
     * Returns the current snake body segments.
     * 
     * @return ArrayList of Points representing the snake's body (head at index 0)
     */
    public ArrayList<Point> getSnake() {
        return snake;
    }

    /**
     * Executes one movement cycle for the AI snake.
     * 
     * This method implements the core AI logic:
     * 1. Calculates the best direction toward the food
     * 2. Validates the move for safety (collision avoidance)
     * 3. If unsafe, finds an alternative safe direction
     * 4. Moves the snake and handles food consumption
     * 
     * The AI behavior varies based on difficulty level set in constructor.
     */
    public void move() {
        Point head = snake.get(0);
        Point newHead = new Point(head);
        
        // Calculate the optimal direction toward the food
        int dx = food.x - head.x;
        int dy = food.y - head.y;
        
        // Find the best movement direction using AI logic
        int bestDirection = getNextDirection(snake, food, playerSnake, width, height);
        
        // Move in the calculated direction
        switch (bestDirection) {
            case KeyEvent.VK_UP: newHead.y--; break;
            case KeyEvent.VK_DOWN: newHead.y++; break;
            case KeyEvent.VK_LEFT: newHead.x--; break;
            case KeyEvent.VK_RIGHT: newHead.x++; break;
        }
        
        // Validate the proposed move for collisions
        if (isValidMove(newHead)) {
            snake.add(0, newHead);
            if (newHead.x == food.x && newHead.y == food.y) {
                // Food consumed - snake grows (don't remove tail)
            } else {
                snake.remove(snake.size() - 1);
            }
            direction = bestDirection;
        } else {
            // If optimal direction is unsafe, find alternative safe direction
            tryAlternativeMove();
        }
    }

    /**
     * Determines the best movement direction based on AI difficulty level.
     * 
     * This method delegates to difficulty-specific algorithms:
     * - Easy: Mostly random movement with occasional food tracking
     * - Medium: Balanced approach with some strategic errors
     * - Hard: Intelligent pathfinding with obstacle avoidance
     * 
     * @param aiSnake The AI snake's body segments
     * @param food The current food position
     * @param playerSnake The player's snake body segments
     * @param width The game board width
     * @param height The game board height
     * @return KeyEvent constant representing the chosen direction (UP, DOWN, LEFT, RIGHT)
     */
    private int getNextDirection(ArrayList<Point> aiSnake, Point food, ArrayList<Point> playerSnake, int width, int height) {
        Point head = aiSnake.get(0);
        int currentDirection = lastDirection;

        switch (difficulty) {
            case 1: // Easy difficulty: Random movement with occasional mistakes
                return getSimpleDirection(head, food);
            case 2: // Medium difficulty: Tracks food but may make errors
                return getMediumDirection(head, food, aiSnake, playerSnake, width, height);
            case 3: // Hard difficulty: Intelligent food tracking with obstacle avoidance
                return getHardDirection(head, food, aiSnake, playerSnake, width, height);
            default:
                return currentDirection;
        }
    }

    /**
     * Implements easy difficulty AI behavior.
     * 
     * Easy AI makes mostly random moves to simulate a beginner player:
     * - 70% chance of random movement in any direction
     * - 30% chance of actually tracking toward the food
     * 
     * This creates an unpredictable but generally weak opponent.
     * 
     * @param head Current position of the AI snake's head
     * @param food Current position of the food
     * @return KeyEvent constant for the chosen movement direction
     */
    private int getSimpleDirection(Point head, Point food) {
        // Easy difficulty: 70% random movement, 30% food tracking
        if (random.nextDouble() < 0.7) {
            // Random movement - makes AI unpredictable but weak
            int[] directions = {KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT};
            return directions[random.nextInt(directions.length)];
        } else {
            // Occasionally track toward food
            return getDirectionTowards(head, food);
        }
    }

    /**
     * Implements medium difficulty AI behavior.
     * 
     * Medium AI provides a balanced challenge:
     * - 80% chance of tracking toward food (if safe)
     * - 20% chance of random movement or when food tracking is unsafe
     * - Includes basic safety checks to avoid immediate collisions
     * 
     * This creates a moderately challenging opponent that makes strategic
     * decisions but still has some unpredictable behavior.
     * 
     * @param head Current position of the AI snake's head
     * @param food Current position of the food
     * @param aiSnake The AI snake's body segments for self-collision checking
     * @param playerSnake The player's snake body segments for collision avoidance
     * @param width Game board width for boundary checking
     * @param height Game board height for boundary checking
     * @return KeyEvent constant for the chosen movement direction
     */
    private int getMediumDirection(Point head, Point food, ArrayList<Point> aiSnake, ArrayList<Point> playerSnake, int width, int height) {
        // Medium difficulty: 80% food tracking, 20% random/safety movement
        if (random.nextDouble() < 0.8) {
            int direction = getDirectionTowards(head, food);
            // Check if the food-tracking move is safe to execute
            if (isSafeMove(head, direction, aiSnake, playerSnake, width, height)) {
                return direction;
            }
        }
        // If food tracking is unsafe or random behavior triggered, find safe alternative
        return getSafeRandomDirection(head, aiSnake, playerSnake, width, height);
    }

    /**
     * Implements hard difficulty AI behavior.
     * 
     * Hard AI provides the most challenging opponent with sophisticated strategies:
     * - Intelligently tracks toward food using optimal pathfinding
     * - Performs comprehensive safety checks for all potential moves
     * - Avoids getting stuck in repetitive movement patterns
     * - Implements fallback strategies when optimal moves are blocked
     * - Considers both self-collision and player collision avoidance
     * 
     * This creates a formidable opponent that requires skill to defeat.
     * 
     * @param head Current position of the AI snake's head
     * @param food Current position of the food
     * @param aiSnake The AI snake's body segments for self-collision checking
     * @param playerSnake The player's snake body segments for collision avoidance
     * @param width Game board width for boundary checking
     * @param height Game board height for boundary checking
     * @return KeyEvent constant for the chosen movement direction
     */
    private int getHardDirection(Point head, Point food, ArrayList<Point> aiSnake, ArrayList<Point> playerSnake, int width, int height) {
        // Hard difficulty: Intelligent food tracking with obstacle avoidance
        int bestDirection = getDirectionTowards(head, food);
        
        // If optimal direction is unsafe, systematically find safe alternatives
        if (!isSafeMove(head, bestDirection, aiSnake, playerSnake, width, height)) {
            // Try all possible directions to find a safe move
            int[] directions = {KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT};
            for (int dir : directions) {
                if (isSafeMove(head, dir, aiSnake, playerSnake, width, height)) {
                    bestDirection = dir;
                    break;
                }
            }
        }

        // Prevent getting stuck in repetitive movement patterns
        if (bestDirection == lastDirection) {
            consecutiveMoves++;
            if (consecutiveMoves >= MAX_CONSECUTIVE_MOVES) {
                // Force direction change if moving in same direction too long
                int[] directions = {KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT};
                for (int dir : directions) {
                    if (dir != lastDirection && isSafeMove(head, dir, aiSnake, playerSnake, width, height)) {
                        bestDirection = dir;
                        consecutiveMoves = 0;
                        break;
                    }
                }
            }
        } else {
            consecutiveMoves = 0;
        }

        lastDirection = bestDirection;
        return bestDirection;
    }

    private int getDirectionTowards(Point head, Point target) {
        int dx = target.x - head.x;
        int dy = target.y - head.y;
        
        // 优先选择距离差较大的方向
        if (Math.abs(dx) > Math.abs(dy)) {
            return dx > 0 ? KeyEvent.VK_RIGHT : KeyEvent.VK_LEFT;
        } else {
            return dy > 0 ? KeyEvent.VK_DOWN : KeyEvent.VK_UP;
        }
    }

    private boolean isSafeMove(Point head, int direction, ArrayList<Point> aiSnake, ArrayList<Point> playerSnake, int width, int height) {
        Point nextHead = new Point(head);
        switch (direction) {
            case KeyEvent.VK_UP: nextHead.y--; break;
            case KeyEvent.VK_DOWN: nextHead.y++; break;
            case KeyEvent.VK_LEFT: nextHead.x--; break;
            case KeyEvent.VK_RIGHT: nextHead.x++; break;
        }

        // Check if hitting wall
        if (nextHead.x < 0 || nextHead.x >= width || nextHead.y < 0 || nextHead.y >= height) {
            return false;
        }

        // Check if hitting self
        for (int i = 1; i < aiSnake.size(); i++) {
            if (aiSnake.get(i).equals(nextHead)) {
                return false;
            }
        }

        // Check if hitting player snake
        for (Point p : playerSnake) {
            if (p.equals(nextHead)) {
                return false;
            }
        }

        return true;
    }

    private int getSafeRandomDirection(Point head, ArrayList<Point> aiSnake, ArrayList<Point> playerSnake, int width, int height) {
        int[] directions = {KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT};
        // 打乱方向数组
        for (int i = directions.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = directions[i];
            directions[i] = directions[j];
            directions[j] = temp;
        }

        // 尝试每个方向，找到第一个安全的移动方向
        for (int dir : directions) {
            if (isSafeMove(head, dir, aiSnake, playerSnake, width, height)) {
                return dir;
            }
        }

        // 如果没有安全的方向，返回当前方向
        return lastDirection;
    }

    private void tryAlternativeMove() {
        Point head = snake.get(0);
        int[] directions = {KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT};
        
        // 随机打乱方向顺序
        for (int i = directions.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = directions[i];
            directions[i] = directions[j];
            directions[j] = temp;
        }
        
        for (int dir : directions) {
            Point newHead = new Point(head);
            switch (dir) {
                case KeyEvent.VK_UP: newHead.y--; break;
                case KeyEvent.VK_DOWN: newHead.y++; break;
                case KeyEvent.VK_LEFT: newHead.x--; break;
                case KeyEvent.VK_RIGHT: newHead.x++; break;
            }
            
            if (isValidMove(newHead)) {
                snake.add(0, newHead);
                snake.remove(snake.size() - 1);
                direction = dir;
                return;
            }
        }
    }

    private boolean isValidMove(Point point) {
        // Check if hitting wall
        if (point.x < 0 || point.x >= width || point.y < 0 || point.y >= height) {
            return false;
        }
        
        // Check if hitting self
        for (int i = 1; i < snake.size(); i++) {
            if (snake.get(i).equals(point)) {
                return false;
            }
        }
        
        // 检查是否撞到障碍物
        for (Point obs : obstacles) {
            if (obs.x == point.x && obs.y == point.y) {
                return false;
            }
        }
        
        // Check if hitting player snake
        if (playerSnake != null) {
            for (Point p : playerSnake) {
                if (p.x == point.x && p.y == point.y) {
                    return false;
                }
            }
        }
        
        return true;
    }
}