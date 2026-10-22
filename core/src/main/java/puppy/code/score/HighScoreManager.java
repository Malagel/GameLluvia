package puppy.code.score;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Application-scoped owner of the local leaderboard.
 */
public final class HighScoreManager {
    public static final int MAX_SCORES = 5;

    private static volatile HighScoreManager instance;

    private final ScoreStore store;
    private final List<Integer> scores = new ArrayList<>();
    private long lastRecordedGameId;

    private HighScoreManager(ScoreStore store) {
        this.store = Objects.requireNonNull(store, "store");
        loadScores();
    }

    public static synchronized HighScoreManager initialize(ScoreStore store) {
        if (instance == null) {
            instance = new HighScoreManager(store);
        }
        return instance;
    }

    public static HighScoreManager getInstance() {
        HighScoreManager current = instance;
        if (current == null) {
            throw new IllegalStateException("HighScoreManager has not been initialized");
        }
        return current;
    }

    /**
     * Ends the application-scoped lifecycle. The persisted scores are unaffected.
     */
    public static synchronized void shutdown() {
        instance = null;
    }

    /**
     * Records one completed game. Equal scores from different games are valid.
     * Game ids are supplied in increasing order by the application. Reusing a game
     * id is ignored, even when that score missed the top five.
     */
    public synchronized boolean recordScore(long completedGameId, int score) {
        if (completedGameId <= 0L) {
            throw new IllegalArgumentException("completedGameId must be positive");
        }
        if (score < 0) {
            throw new IllegalArgumentException("score cannot be negative");
        }
        if (completedGameId <= lastRecordedGameId) {
            return false;
        }
        lastRecordedGameId = completedGameId;

        scores.add(score);
        normalizeRanking();
        store.save(serializeScores());
        return true;
    }

    public synchronized List<Integer> getScores() {
        return Collections.unmodifiableList(new ArrayList<>(scores));
    }

    private void loadScores() {
        String saved = store.load();
        if (saved == null || saved.trim().isEmpty()) {
            return;
        }

        for (String value : saved.split(",")) {
            try {
                int score = Integer.parseInt(value.trim());
                if (score >= 0) {
                    scores.add(score);
                }
            } catch (NumberFormatException ignored) {
                // Keep valid entries and discard malformed values.
            }
        }

        normalizeRanking();
        String normalized = serializeScores();
        if (!normalized.equals(saved)) {
            store.save(normalized);
        }
    }

    private void normalizeRanking() {
        scores.sort(Collections.reverseOrder());
        if (scores.size() > MAX_SCORES) {
            scores.subList(MAX_SCORES, scores.size()).clear();
        }
    }

    private String serializeScores() {
        StringBuilder serialized = new StringBuilder();
        for (int index = 0; index < scores.size(); index++) {
            if (index > 0) {
                serialized.append(',');
            }
            serialized.append(scores.get(index));
        }
        return serialized.toString();
    }
}
