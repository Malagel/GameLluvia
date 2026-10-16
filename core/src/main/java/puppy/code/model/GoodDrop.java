package puppy.code.model;

public final class GoodDrop extends FallingItem {
    private static final int SCORE_VALUE = 10;

    public GoodDrop(float x, float y) {
        super(x, y);
    }

    @Override
    public Type getType() {
        return Type.GOOD;
    }

    @Override
    public void applyTo(Player player) {
        player.addScore(SCORE_VALUE);
    }
}
