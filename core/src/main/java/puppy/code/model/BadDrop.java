package puppy.code.model;

public final class BadDrop extends FallingItem {
    public BadDrop(float x, float y) {
        this(x, y, new DamageEffect());
    }

    public BadDrop(float x, float y, DropEffect effect) {
        super(x, y, effect);
    }

    @Override
    public Type getType() {
        return Type.BAD;
    }
}
