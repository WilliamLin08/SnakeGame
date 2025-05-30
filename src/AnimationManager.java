import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Manages animations for the Snake Game including food scaling, rotation, and screen shake effects.
 * Provides smooth transitions and visual feedback for game events.
 */
public class AnimationManager {
    private List<AnimatedElement> elements;
    private ScreenShake screenShake;
    private long lastUpdateTime;
    
    public AnimationManager() {
        this.elements = new ArrayList<>();
        this.screenShake = new ScreenShake();
        this.lastUpdateTime = System.currentTimeMillis();
    }
    
    /**
     * Updates all active animations
     */
    public void update() {
        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - lastUpdateTime) / 1000.0f;
        lastUpdateTime = currentTime;
        
        // Update animated elements
        elements.removeIf(element -> {
            element.update(deltaTime);
            return element.isFinished();
        });
        
        // Update screen shake
        screenShake.update(deltaTime);
    }
    
    /**
     * Starts a food consumption animation
     */
    public void startFoodAnimation(int x, int y) {
        AnimatedElement scaleAnimation = new AnimatedElement(x, y, 1000); // 1 second
        scaleAnimation.setScaleAnimation(1.0f, 1.5f, AnimatedElement.EasingType.EASE_OUT);
        elements.add(scaleAnimation);
    }
    
    /**
     * Starts a food consumption animation (without specific coordinates)
     */
    public void startFoodConsumptionAnimation() {
        // This method can be called when food position is managed elsewhere
        // For now, we'll create a generic animation that affects the last food
        AnimatedElement scaleAnimation = new AnimatedElement(0, 0, 500); // 0.5 second
        scaleAnimation.setScaleAnimation(1.0f, 1.3f, AnimatedElement.EasingType.EASE_OUT);
        elements.add(scaleAnimation);
    }
    
    /**
     * Starts screen shake effect
     */
    public void startScreenShake(float intensity, float duration) {
        screenShake.start(intensity, duration);
    }
    
    /**
     * Triggers screen shake effect (alias for startScreenShake)
     */
    public void triggerScreenShake(float intensity, float duration) {
        screenShake.start(intensity, duration);
    }
    
    /**
     * Gets the current food scale for animation
     */
    public float getFoodScale(int x, int y) {
        for (AnimatedElement element : elements) {
            if (element.x == x && element.y == y) {
                return element.getCurrentScale();
            }
        }
        return 1.0f;
    }
    
    /**
     * Gets the current food scale for animation (no parameters - uses last animated food)
     */
    public float getFoodScale() {
        if (!elements.isEmpty()) {
            return elements.get(elements.size() - 1).getCurrentScale();
        }
        return 1.0f;
    }
    
    /**
     * Gets the current food rotation for animation
     */
    public float getFoodRotation(int x, int y) {
        for (AnimatedElement element : elements) {
            if (element.x == x && element.y == y) {
                return element.getCurrentRotation();
            }
        }
        return 0.0f;
    }
    
    /**
     * Gets the current food rotation for animation (no parameters - uses last animated food)
     */
    public float getFoodRotation() {
        if (!elements.isEmpty()) {
            return elements.get(elements.size() - 1).getCurrentRotation();
        }
        return 0.0f;
    }
    
    /**
     * Checks if any food animation is currently active
     */
    public boolean isFoodAnimationActive() {
        return !elements.isEmpty();
    }
    
    /**
     * Gets screen shake offset
     */
    public Point getScreenShakeOffset() {
        return screenShake.getOffset();
    }
    
    /**
     * Represents an animated element in the game
     */
    public static class AnimatedElement {
        public int x, y;
        private long duration;
        private long startTime;
        private float startScale, endScale;
        private float startRotation, endRotation;
        private EasingType easingType;
        
        public enum EasingType {
            LINEAR,
            EASE_IN,
            EASE_OUT,
            EASE_IN_OUT
        }
        
        public AnimatedElement(int x, int y, long duration) {
            this.x = x;
            this.y = y;
            this.duration = duration;
            this.startTime = System.currentTimeMillis();
            this.startScale = 1.0f;
            this.endScale = 1.0f;
            this.startRotation = 0.0f;
            this.endRotation = 0.0f;
            this.easingType = EasingType.LINEAR;
        }
        
        public void setScaleAnimation(float startScale, float endScale, EasingType easing) {
            this.startScale = startScale;
            this.endScale = endScale;
            this.easingType = easing;
        }
        
        public void setRotationAnimation(float startRotation, float endRotation) {
            this.startRotation = startRotation;
            this.endRotation = endRotation;
        }
        
        public void update(float deltaTime) {
            // Animation is time-based, no need for deltaTime in this implementation
        }
        
        public boolean isFinished() {
            return System.currentTimeMillis() - startTime >= duration;
        }
        
        public float getCurrentScale() {
            float progress = Math.min(1.0f, (System.currentTimeMillis() - startTime) / (float) duration);
            progress = applyEasing(progress);
            return startScale + (endScale - startScale) * progress;
        }
        
        public float getCurrentRotation() {
            float progress = Math.min(1.0f, (System.currentTimeMillis() - startTime) / (float) duration);
            return startRotation + (endRotation - startRotation) * progress;
        }
        
        private float applyEasing(float t) {
            switch (easingType) {
                case EASE_IN:
                    return t * t;
                case EASE_OUT:
                    return 1 - (1 - t) * (1 - t);
                case EASE_IN_OUT:
                    return t < 0.5f ? 2 * t * t : 1 - 2 * (1 - t) * (1 - t);
                default:
                    return t;
            }
        }
    }
    
    /**
     * Handles screen shake effects
     */
    public static class ScreenShake {
        private float intensity;
        private float duration;
        private float timeRemaining;
        private Random random;
        
        public ScreenShake() {
            this.random = new Random();
            this.intensity = 0;
            this.duration = 0;
            this.timeRemaining = 0;
        }
        
        public void start(float intensity, float duration) {
            this.intensity = intensity;
            this.duration = duration;
            this.timeRemaining = duration;
        }
        
        public void update(float deltaTime) {
            if (timeRemaining > 0) {
                timeRemaining -= deltaTime;
                if (timeRemaining <= 0) {
                    timeRemaining = 0;
                    intensity = 0;
                }
            }
        }
        
        public Point getOffset() {
            if (timeRemaining <= 0) {
                return new Point(0, 0);
            }
            
            float currentIntensity = intensity * (timeRemaining / duration);
            int offsetX = (int) ((random.nextFloat() - 0.5f) * 2 * currentIntensity);
            int offsetY = (int) ((random.nextFloat() - 0.5f) * 2 * currentIntensity);
            
            return new Point(offsetX, offsetY);
        }
        
        public boolean isActive() {
            return timeRemaining > 0;
        }
    }
}