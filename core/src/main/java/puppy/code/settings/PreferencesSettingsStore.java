package puppy.code.settings;

import com.badlogic.gdx.Preferences;
import java.util.Objects;

public final class PreferencesSettingsStore implements SettingsStore {
    private final Preferences preferences;

    public PreferencesSettingsStore(Preferences preferences) {
        this.preferences = Objects.requireNonNull(preferences, "preferences");
    }

    @Override
    public int loadInt(String key, int defaultValue) {
        return preferences.getInteger(key, defaultValue);
    }

    @Override
    public void saveInt(String key, int value) {
        preferences.putInteger(key, value);
        preferences.flush();
    }
}
