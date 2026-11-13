package puppy.code.model;

/**
 * Awards a configurable number of points to the player.
 */
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
}
