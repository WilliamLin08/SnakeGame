import java.awt.*;
import java.util.Random;

/**
 * Represents different types of food items in the Snake Game.
 * Each food type has unique properties including point values, colors, and special effects.
 * 
 * Food Types:
 * - NORMAL: Standard food (10 points)
 * - BONUS: High-value food (25 points) 
 * - SPEED: Temporarily increases snake speed (15 points)
 * - SLOW: Temporarily decreases snake speed (15 points)
 * - MEGA: Very high-value food (50 points)
 * - SHRINK: Reduces snake length by 1 segment (5 points)
 * 
 * @author Snake Game Development Team
 * @version 1.0
 */
public enum FoodType {
    NORMAL(10, new Color(255, 69, 0), "Normal Food", 0),
    BONUS(25, new Color(255, 215, 0), "Bonus Food", 0),
    SPEED(15, new Color(0, 255, 255), "Speed Boost", 3000), // 3 second effect
    SLOW(15, new Color(128, 0, 128), "Slow Motion", 3000),  // 3 second effect
    MEGA(50, new Color(255, 20, 147), "Mega Food", 0),
    SHRINK(5, new Color(34, 139, 34), "Shrink Food", 0);
    
    /** Points awarded when this food type is consumed */
    public final int points;
    
    /** Color used to render this food type */
    public final Color color;
    
    /** Display name for this food type */
    public final String name;
    
    /** Duration of special effect in milliseconds (0 = no effect) */
    public final int effectDuration;
    
    /** Random generator for food type selection */
    private static final Random random = new Random();
    
    /**
     * Constructor for FoodType enum.
     * 
     * @param points Points awarded when consumed
     * @param color Color for rendering
     * @param name Display name
     * @param effectDuration Duration of special effect in milliseconds
     */
    FoodType(int points, Color color, String name, int effectDuration) {
        this.points = points;
        this.color = color;
        this.name = name;
        this.effectDuration = effectDuration;
    }
    
    /**
     * Generates a random food type with weighted probabilities.
     * Normal food is most common, special foods are rarer.
     * 
     * @return A randomly selected FoodType
     */
    public static FoodType getRandomFoodType() {
        int rand = random.nextInt(100);
        
        if (rand < 50) {
            return NORMAL;      // 50% chance
        } else if (rand < 70) {
            return BONUS;       // 20% chance
        } else if (rand < 80) {
            return SPEED;       // 10% chance
        } else if (rand < 90) {
            return SLOW;        // 10% chance
        } else if (rand < 97) {
            return SHRINK;      // 7% chance
        } else {
            return MEGA;        // 3% chance
        }
    }
    
    /**
     * Checks if this food type has a special effect.
     * 
     * @return true if the food has a timed effect, false otherwise
     */
    public boolean hasEffect() {
        return effectDuration > 0;
    }
    
    /**
     * Gets the appropriate color for this food type, potentially modified by theme.
     * 
     * @param theme The current game theme
     * @return The color to use for rendering this food
     */
    public Color getThemeColor(Theme theme) {
        // For normal food, use theme's food color
        if (this == NORMAL) {
            return theme.foodColor;
        }
        // For special foods, use their unique colors
        return this.color;
    }
    
    /**
     * Gets a description of what this food type does.
     * 
     * @return A string describing the food's effect
     */
    public String getDescription() {
        switch (this) {
            case NORMAL:
                return "Standard food item";
            case BONUS:
                return "Worth extra points!";
            case SPEED:
                return "Increases snake speed temporarily";
            case SLOW:
                return "Slows down snake temporarily";
            case MEGA:
                return "Massive point bonus!";
            case SHRINK:
                return "Reduces snake length by 1";
            default:
                return "Unknown food type";
        }
    }
}