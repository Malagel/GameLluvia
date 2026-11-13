package puppy.code.model;

public final class BadDrop extends FallingItem {
    // Strategies are immutable, so every default drop can share one instance.
    private static final DropEffect DEFAULT_EFFECT = new DamageEffect();

    public BadDrop(float x, float y) {
        this(x, y, DEFAULT_EFFECT);
    }

    public BadDrop(float x, float y, DropEffect effect) {
        super(x, y, effect);
    }

    @Override
    public Type getType() {
        return Type.BAD;
    }
}
