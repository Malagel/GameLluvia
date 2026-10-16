package puppy.code.model;

public final class BadDrop extends FallingItem {
    public BadDrop(float x, float y) {
        super(x, y);
    }

    @Override
    public Type getType() {
        return Type.BAD;
    }

    @Override
    public void applyTo(Player player) {
        player.takeDamage();
    }
}
