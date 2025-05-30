import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a visual theme for the Snake Game.
 * 
 * This class defines the color scheme and visual appearance of the game.
 * Each theme contains colors for all game elements including background,
 * snake, food, grid lines, walls, obstacles, and text.
 * 
 * The class provides a collection of predefined themes ranging from
 * classic arcade styles to modern artistic designs:
 * - Classic: Traditional green snake on dark background
 * - Ocean: Blue aquatic theme with coral accents
 * - Forest: Natural green theme with earth tones
 * - Neon: Bright electric colors for a cyberpunk feel
 * - And many more creative variations
 * 
 * Themes can be selected individually or chosen randomly for variety.
 * 
 * @author Snake Game Development Team
 * @version 1.0
 */
public class Theme {
    /** The display name of this theme (e.g., "Classic", "Ocean") */
    public String name;
    
    /** Background color for the game board */
    public Color backgroundColor;
    
    /** Color used for rendering the snake's body segments */
    public Color snakeColor;
    
    /** Color used for rendering the food items */
    public Color foodColor;
    
    /** Color used for the grid lines that divide the game board */
    public Color gridColor;
    
    /** Color used for rendering walls and boundaries */
    public Color wallColor;
    
    /** Color used for rendering obstacles on the game board */
    public Color obstacleColor;
    
    /** Color used for all text elements (score, instructions, etc.) */
    public Color textColor;
    
    /**
     * Creates a new theme with the specified color scheme.
     * 
     * @param name Display name for this theme
     * @param backgroundColor Background color for the game board
     * @param snakeColor Color for the snake's body
     * @param foodColor Color for food items
     * @param gridColor Color for grid lines
     * @param wallColor Color for walls and boundaries
     * @param obstacleColor Color for obstacles
     * @param textColor Color for text elements
     */
    public Theme(String name, Color backgroundColor, Color snakeColor, Color foodColor, 
                 Color gridColor, Color wallColor, Color obstacleColor, Color textColor) {
        this.name = name;
        this.backgroundColor = backgroundColor;
        this.snakeColor = snakeColor;
        this.foodColor = foodColor;
        this.gridColor = gridColor;
        this.wallColor = wallColor;
        this.obstacleColor = obstacleColor;
        this.textColor = textColor;
    }
    
    // Predefined themes
    public static final List<Theme> THEMES = new ArrayList<>();
    
    static {
        // Classic theme (original)
        THEMES.add(new Theme("Classic", 
            new Color(20, 20, 20),      // background
            new Color(50, 205, 50),     // snake
            new Color(255, 69, 0),      // food
            new Color(40, 40, 40),      // grid
            new Color(139, 69, 19),     // wall
            new Color(120, 120, 120),   // obstacle
            Color.WHITE                 // text
        ));
        
        // Ocean theme - Enhanced with deeper blues and coral accents
        THEMES.add(new Theme("Ocean", 
            new Color(13, 27, 42),      // deep ocean blue background
            new Color(64, 224, 208),    // turquoise snake
            new Color(255, 127, 80),    // coral food
            new Color(25, 42, 86),      // deeper blue grid
            new Color(72, 61, 139),     // dark slate blue wall
            new Color(95, 158, 160),    // cadet blue obstacle
            new Color(176, 224, 230)    // powder blue text
        ));
        
        // Forest theme - Enhanced with richer greens and autumn colors
        THEMES.add(new Theme("Forest", 
            new Color(22, 33, 22),      // deep forest background
            new Color(34, 139, 34),     // forest green snake
            new Color(220, 20, 60),     // crimson berry food
            new Color(46, 69, 46),      // darker forest grid
            new Color(139, 69, 19),     // saddle brown wall
            new Color(107, 142, 35),    // olive drab obstacle
            new Color(144, 238, 144)    // light green text
        ));
        
        // Neon theme - Enhanced with electric colors
        THEMES.add(new Theme("Neon", 
            new Color(5, 5, 15),        // almost black with blue tint
            new Color(0, 255, 127),     // spring green snake
            new Color(255, 0, 255),     // magenta food
            new Color(20, 20, 40),      // dark blue-gray grid
            new Color(75, 0, 130),      // indigo wall
            new Color(255, 215, 0),     // gold obstacle
            new Color(0, 255, 255)      // cyan text
        ));
        
        // Sunset theme - Enhanced with warm gradient colors
        THEMES.add(new Theme("Sunset", 
            new Color(25, 25, 112),     // midnight blue background
            new Color(255, 165, 0),     // orange snake
            new Color(255, 69, 0),      // red-orange food
            new Color(72, 61, 139),     // dark slate blue grid
            new Color(160, 82, 45),     // saddle brown wall
            new Color(205, 92, 92),     // indian red obstacle
            new Color(255, 218, 185)    // peach puff text
        ));
        
        // Retro theme - Enhanced with classic arcade colors
        THEMES.add(new Theme("Retro", 
            new Color(0, 0, 0),         // pure black background
            new Color(50, 205, 50),     // lime green snake
            new Color(255, 255, 0),     // yellow food
            new Color(64, 64, 64),      // dark gray grid
            new Color(255, 255, 255),   // white wall
            new Color(169, 169, 169),   // dark gray obstacle
            new Color(0, 255, 0)        // lime green text
        ));
        
        // Cyberpunk theme - Futuristic purple and pink
        THEMES.add(new Theme("Cyberpunk", 
            new Color(16, 16, 32),      // dark purple background
            new Color(255, 0, 255),     // magenta snake
            new Color(0, 255, 255),     // cyan food
            new Color(32, 32, 64),      // purple grid
            new Color(138, 43, 226),    // blue violet wall
            new Color(75, 0, 130),      // indigo obstacle
            new Color(255, 20, 147)     // deep pink text
        ));
        
        // Sakura theme - Japanese cherry blossom inspired
        THEMES.add(new Theme("Sakura", 
            new Color(25, 25, 35),      // dark navy background
            new Color(255, 182, 193),   // light pink snake
            new Color(255, 105, 180),   // hot pink food
            new Color(47, 79, 79),      // dark slate gray grid
            new Color(139, 69, 19),     // saddle brown wall
            new Color(128, 128, 128),   // gray obstacle
            new Color(255, 240, 245)    // lavender blush text
        ));
        
        // Arctic theme - Cool blues and whites
        THEMES.add(new Theme("Arctic", 
            new Color(25, 25, 112),     // midnight blue background
            new Color(135, 206, 250),   // light sky blue snake
            new Color(255, 255, 255),   // white food
            new Color(70, 130, 180),    // steel blue grid
            new Color(176, 196, 222),   // light steel blue wall
            new Color(119, 136, 153),   // light slate gray obstacle
            new Color(240, 248, 255)    // alice blue text
        ));
        
        // Lava theme - Hot reds and oranges
        THEMES.add(new Theme("Lava", 
            new Color(25, 25, 25),      // charcoal background
            new Color(255, 69, 0),      // red-orange snake
            new Color(255, 215, 0),     // gold food
            new Color(139, 0, 0),       // dark red grid
            new Color(160, 82, 45),     // saddle brown wall
            new Color(105, 105, 105),   // dim gray obstacle
            new Color(255, 165, 0)      // orange text
        ));
        
        // Galaxy theme - Deep space colors
        THEMES.add(new Theme("Galaxy", 
            new Color(8, 8, 24),        // deep space background
            new Color(147, 0, 211),     // dark violet snake
            new Color(255, 215, 0),     // gold star food
            new Color(25, 25, 112),     // midnight blue grid
            new Color(72, 61, 139),     // dark slate blue wall
            new Color(123, 104, 238),   // medium slate blue obstacle
            new Color(230, 230, 250)    // lavender text
        ));
        
        // Emerald theme - Luxurious greens and golds
        THEMES.add(new Theme("Emerald", 
            new Color(0, 20, 0),        // very dark green background
            new Color(0, 201, 87),      // emerald green snake
            new Color(255, 215, 0),     // gold food
            new Color(0, 100, 0),       // dark green grid
            new Color(184, 134, 11),    // dark goldenrod wall
            new Color(46, 125, 50),     // dark green obstacle
            new Color(144, 238, 144)    // light green text
        ));
        
        // Vaporwave theme - Retro aesthetic with pastels
        THEMES.add(new Theme("Vaporwave", 
            new Color(16, 16, 48),      // dark blue-purple background
            new Color(255, 20, 147),    // deep pink snake
            new Color(0, 255, 255),     // cyan food
            new Color(75, 0, 130),      // indigo grid
            new Color(138, 43, 226),    // blue violet wall
            new Color(255, 105, 180),   // hot pink obstacle
            new Color(255, 182, 193)    // light pink text
        ));
    }
    
    /**
     * Returns a randomly selected theme from all available themes.
     * 
     * This method provides variety by randomly selecting from the complete
     * collection of themes. Each theme has an equal probability of being chosen.
     * 
     * @return A randomly selected Theme object
     */
    public static Theme getRandomTheme() {
        Random random = new Random();
        return THEMES.get(random.nextInt(THEMES.size()));
    }
    
    /**
     * Returns a list of all available themes.
     * 
     * This method creates and returns a comprehensive collection of predefined
     * themes, each with carefully chosen color schemes to provide different
     * visual experiences. Themes range from classic arcade styles to modern
     * artistic designs.
     * 
     * @return A list containing all available Theme objects
     */
    public static List<Theme> getAllThemes() {
        return new ArrayList<>(THEMES);
    }
    
    /**
     * Retrieves a theme by its name.
     * 
     * Searches through all available themes to find one with the specified name.
     * The search is case-sensitive and matches the exact theme name.
     * 
     * @param name The name of the theme to retrieve (e.g., "Classic", "Ocean")
     * @return The Theme object with the matching name, or the first theme
     *         (Classic) if no match is found
     */
    public static Theme getThemeByName(String name) {
        return THEMES.stream()
                .filter(theme -> theme.name.equals(name))
                .findFirst()
                .orElse(THEMES.get(0)); // Default to first theme if not found
    }
}