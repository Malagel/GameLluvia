package puppy.code.model;

/**
 * A beneficial drop. It falls straight down, the inherited default motion, and awards
 * points unless a different strategy is selected when it is created.
 */
public final class GoodDrop extends FallingItem {
    private static final int SCORE_VALUE = 10;
    // Strategies are immutable, so every default drop can share one instance.
    private static final DropEffect DEFAULT_EFFECT = new ScoreEffect(SCORE_VALUE);

    public GoodDrop(float x, float y) {
        this(x, y, DEFAULT_EFFECT);
    }

    public GoodDrop(float x, float y, DropEffect effect) {
        super(x, y, effect);
    }

    @Override
    public Type getType() {
        return Type.GOOD;
    }
}
