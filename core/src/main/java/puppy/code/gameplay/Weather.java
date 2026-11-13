package puppy.code.gameplay;

/**
 * Phases of the rain. Each phase starts at a score, so the current weather follows from the
 * score alone and a new phase is a new constant rather than another condition in the code
 * that uses it. Scores never decrease, so a phase, once reached, lasts until the game ends.
 */
public enum Weather {
    CALM(0),
    STORM(300);

    private final int startingScore;

    Weather(int startingScore) {
        this.startingScore = startingScore;
    }

    int getStartingScore() {
        return startingScore;
    }

    /**
     * The latest phase whose starting score has been reached. Constants are declared in
     * increasing order of starting score.
     */
    static Weather forScore(int score) {
        Weather reached = CALM;
        for (Weather weather : values()) {
            if (score >= weather.startingScore) {
                reached = weather;
            }
        }
        return reached;
    }
}
