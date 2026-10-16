package puppy.code.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public final class Player {
    public static final float SIZE = 64.0f;

    private static final float START_X = 800.0f / 2.0f - SIZE / 2.0f;
    private static final float START_Y = 20.0f;
    private static final float SPEED = 400.0f;
    private static final float MIN_X = 0.0f;
    private static final float MAX_X = 800.0f - SIZE;
    private static final int STARTING_LIVES = 3;
    private static final int HURT_DURATION_FRAMES = 50;

    private final Rectangle bounds;
    private int lives;
    private int score;
    private int hurtFramesRemaining;

    public Player() {
        bounds = new Rectangle(START_X, START_Y, SIZE, SIZE);
        lives = STARTING_LIVES;
    }

    public void move(boolean movingLeft, boolean movingRight, float delta) {
        if (movingLeft) {
            bounds.x -= SPEED * delta;
        }
        if (movingRight) {
            bounds.x += SPEED * delta;
        }
        bounds.x = MathUtils.clamp(bounds.x, MIN_X, MAX_X);
    }

    public void addScore(int points) {
        score += points;
    }

    public void takeDamage() {
        lives = Math.max(0, lives - 1);
        hurtFramesRemaining = HURT_DURATION_FRAMES;
    }

    public boolean isHurt() {
        return hurtFramesRemaining > 0;
    }

    public void advanceHurtAnimation() {
        if (hurtFramesRemaining > 0) {
            hurtFramesRemaining--;
        }
    }

    public boolean hasNoLives() {
        return lives <= 0;
    }

    public int getLives() {
        return lives;
    }

    public int getScore() {
        return score;
    }

    public float getX() {
        return bounds.x;
    }

    public float getY() {
        return bounds.y;
    }

    boolean overlaps(Rectangle otherBounds) {
        return bounds.overlaps(otherBounds);
    }
}
