package puppy.code.settings;

/**
 * Persistence boundary used by {@link AudioSettings}.
 *
 * <p>Keeping storage behind this interface lets the volume rules be tested without
 * starting LibGDX.</p>
 */
public interface SettingsStore {
    int loadInt(String key, int defaultValue);

    void saveInt(String key, int value);
}
