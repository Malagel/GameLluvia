package puppy.code.model;

import com.badlogic.gdx.math.Rectangle;

public abstract class FallingItem {
    public static final float SIZE = 64.0f;

    private static final float FALL_SPEED = 300.0f;

    private final Rectangle bounds;

    protected FallingItem(float x, float y) {
        bounds = new Rectangle(x, y, SIZE, SIZE);
    }

    public void fall(float delta) {
        bounds.y -= FALL_SPEED * delta;
    }

    public boolean isBelowWorld() {
        return bounds.y + SIZE < 0.0f;
    }

    public boolean overlaps(Player player) {
        return player.overlaps(bounds);
    }

    public float getX() {
        return bounds.x;
    }

    public float getY() {
        return bounds.y;
    }

    public abstract Type getType();

    public abstract void applyTo(Player player);

    public enum Type {
        GOOD,
        BAD
    }
}
