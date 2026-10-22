package puppy.code.model;

import com.badlogic.gdx.math.Rectangle;
import java.util.Objects;

public abstract class FallingItem {
    public static final float SIZE = 48.0f;

    private static final float FALL_SPEED = 225.0f;

    private final Rectangle bounds;
    private final DropEffect effect;

    protected FallingItem(float x, float y, DropEffect effect) {
        bounds = new Rectangle(x, y, SIZE, SIZE);
        this.effect = Objects.requireNonNull(effect, "effect");
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

    public final void applyTo(Player player) {
        effect.apply(player);
    }

    public enum Type {
        GOOD,
        BAD
    }
}
