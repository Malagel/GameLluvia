package puppy.code.model;

/**
 * A harmful drop. Besides its harmful default effect, it is blown by the wind: it sways
 * from side to side around the column where it spawned, so it is harder to dodge and can
 * be told apart from beneficial drops by its motion as well as its colour.
 */
public final class BadDrop extends FallingItem {
    public static final DropAppearance APPEARANCE = new DropAppearance("harmful drop");

    // Strategies are immutable, so every default drop can share one instance.
    private static final DropEffect DEFAULT_EFFECT = new DamageEffect();

    static final float SWAY_AMPLITUDE = 20.0f;
    static final float SWAY_ANGULAR_SPEED = 5.0f;

    private float swayTime;

    public BadDrop(float x, float y) {
        this(x, y, DEFAULT_EFFECT);
    }

    public BadDrop(float x, float y, DropEffect effect) {
        super(x, y, effect);
    }

    @Override
    public DropAppearance getAppearance() {
        return APPEARANCE;
    }

    /**
     * Follows a sine wave of fixed amplitude. Returning the change in offset, rather than a
     * velocity, keeps the motion exact for any frame duration.
     */
    @Override
    protected float horizontalDrift(float delta) {
        float previousOffset = swayOffset(swayTime);
        swayTime += delta;
        return swayOffset(swayTime) - previousOffset;
    }

    private static float swayOffset(float time) {
        return SWAY_AMPLITUDE * (float) Math.sin(SWAY_ANGULAR_SPEED * time);
    }
}
