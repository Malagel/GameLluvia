package puppy.code.score;

import com.badlogic.gdx.Preferences;
import java.util.Objects;

public final class PreferencesScoreStore implements ScoreStore {
    private static final String SCORES_KEY = "scores";

    private final Preferences preferences;

    public PreferencesScoreStore(Preferences preferences) {
        this.preferences = Objects.requireNonNull(preferences, "preferences");
    }

    @Override
    public String load() {
        return preferences.getString(SCORES_KEY, "");
    }

    @Override
    public void save(String serializedScores) {
        preferences.putString(SCORES_KEY, serializedScores);
        preferences.flush();
    }
}
