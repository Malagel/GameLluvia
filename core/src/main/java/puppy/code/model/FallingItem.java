package puppy.code.model;

import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    public final List<GameplayEvent> applyTo(Player player) {
        List<GameplayEvent> events = Objects.requireNonNull(
                effect.apply(Objects.requireNonNull(player, "player")),
                "effect events");
        for (GameplayEvent event : events) {
            Objects.requireNonNull(event, "effect event");
        }
        return Collections.unmodifiableList(new ArrayList<>(events));
    }

    public enum Type {
        GOOD,
        BAD
    }
}
