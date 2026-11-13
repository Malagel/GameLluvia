package puppy.code.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import java.util.Objects;
import puppy.code.GameConfig;

/**
 * Something that falls from the sky and can be caught by the player.
 *
 * <p>The two axes of variation use different mechanisms on purpose. What happens when the
 * item is caught is composed through a {@link DropEffect} strategy, chosen when the item is
 * created and replaceable while it falls. How the item moves is intrinsic to its kind, so
 * subclasses vary it through the {@link #horizontalDrift(float)} hook of the
 * {@link #fall(float)} template.</p>
 */
public abstract class FallingItem {
    public static final float SIZE = 48.0f;

    private static final float FALL_SPEED = 225.0f;
    private static final float MAX_X = GameConfig.WORLD_WIDTH - SIZE;

    private final Rectangle bounds;
    private DropEffect effect;

    protected FallingItem(float x, float y, DropEffect effect) {
        bounds = new Rectangle(x, y, SIZE, SIZE);
        setEffect(effect);
    }

    /**
     * Replaces this item's strategy. The next time the item is caught, the new effect is
     * the one applied.
     */
    public final void setEffect(DropEffect effect) {
        this.effect = Objects.requireNonNull(effect, "effect");
    }

    /**
     * Switches to the stronger version of the current effect when the rain intensifies.
     * Each strategy decides what its stronger version is, so no type checks are needed.
     */
    public final void intensify() {
        setEffect(effect.intensified());
    }

    /**
     * Advances the item one step: every item falls at the same speed, and its subclass
     * decides any horizontal movement. The item never leaves the world horizontally.
     */
    public final void fall(float delta) {
        bounds.y -= FALL_SPEED * delta;
        float drift = horizontalDrift(delta);
        if (drift != 0.0f) {
            bounds.x = MathUtils.clamp(bounds.x + drift, 0.0f, MAX_X);
        }
    }

    /**
     * Returns the horizontal displacement for one fall step of {@code delta} seconds. It is
     * called exactly once per step, so implementations may advance their own motion state.
     * Items fall straight down by default.
     */
    protected float horizontalDrift(float delta) {
        return 0.0f;
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

    /**
     * The visual identity of this kind of item, declared by each subclass.
     */
    public abstract DropAppearance getAppearance();

    /**
     * Applies this item's strategy to the player and returns the single outcome it caused.
     */
    public final GameplayEvent applyTo(Player player) {
        return Objects.requireNonNull(
                effect.apply(Objects.requireNonNull(player, "player")),
                "effect event");
    }
}
