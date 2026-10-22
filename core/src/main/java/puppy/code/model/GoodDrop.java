package puppy.code.model;

public final class GoodDrop extends FallingItem {
    private static final int SCORE_VALUE = 10;

    public GoodDrop(float x, float y) {
        this(x, y, new ScoreEffect(SCORE_VALUE));
    }

    public GoodDrop(float x, float y, DropEffect effect) {
        super(x, y, effect);
    }

    @Override
    public Type getType() {
        return Type.GOOD;
    }
}
