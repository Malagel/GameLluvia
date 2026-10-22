package puppy.code.score;

/**
 * Persistence boundary used by {@link HighScoreManager}.
 *
 * <p>The manager owns the serialized format so ranking rules can be exercised in
 * tests without starting LibGDX.</p>
 */
public interface ScoreStore {
    String load();

    void save(String serializedScores);
}
