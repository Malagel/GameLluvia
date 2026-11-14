package puppy.code.model;

public final class ScoreEffect implements DropEffect {
    private final int points;

    public ScoreEffect(int points) {
        if (points <= 0) {
            throw new IllegalArgumentException("points must be greater than zero");
        }
        this.points = points;
    }

    @Override
    public GameplayEvent apply(Player player) {
        player.addScore(points);
        return GameplayEvent.DROP_COLLECTED;
    }

    /**
     * Awards double points, saturating at {@link Integer#MAX_VALUE}.
     */
    @Override
    public DropEffect intensified() {
        return new ScoreEffect((int) Math.min(Integer.MAX_VALUE, 2L * points));
    }
}
