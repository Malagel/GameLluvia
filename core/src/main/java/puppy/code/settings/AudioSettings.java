package puppy.code.settings;

import java.util.Objects;

/**
 * Player-selected volumes for music and sound effects.
 *
 * <p>Volumes are kept as whole percentages in steps of {@link #STEP_PERCENT} so repeated
 * changes never accumulate floating point drift. Every change is persisted immediately;
 * a failing store keeps the in-memory value so the game can continue.</p>
 */
public final class AudioSettings {
    public static final int MIN_PERCENT = 0;
    public static final int MAX_PERCENT = 100;
    public static final int STEP_PERCENT = 10;

    static final String MUSIC_KEY = "musicVolume";
    static final String EFFECTS_KEY = "effectsVolume";

    private final SettingsStore store;
    private int musicPercent;
    private int effectsPercent;

    public AudioSettings(SettingsStore store) {
        this.store = Objects.requireNonNull(store, "store");
        musicPercent = normalize(store.loadInt(MUSIC_KEY, MAX_PERCENT));
        effectsPercent = normalize(store.loadInt(EFFECTS_KEY, MAX_PERCENT));
    }

    public int getMusicPercent() {
        return musicPercent;
    }

    public int getEffectsPercent() {
        return effectsPercent;
    }

    /** Music volume in the 0..1 range expected by LibGDX. */
    public float getMusicVolume() {
        return musicPercent / (float) MAX_PERCENT;
    }

    /** Sound effect volume in the 0..1 range expected by LibGDX. */
    public float getEffectsVolume() {
        return effectsPercent / (float) MAX_PERCENT;
    }

    public void increaseMusic() {
        setMusicPercent(musicPercent + STEP_PERCENT);
    }

    public void decreaseMusic() {
        setMusicPercent(musicPercent - STEP_PERCENT);
    }

    public void increaseEffects() {
        setEffectsPercent(effectsPercent + STEP_PERCENT);
    }

    public void decreaseEffects() {
        setEffectsPercent(effectsPercent - STEP_PERCENT);
    }

    private void setMusicPercent(int percent) {
        musicPercent = normalize(percent);
        save(MUSIC_KEY, musicPercent);
    }

    private void setEffectsPercent(int percent) {
        effectsPercent = normalize(percent);
        save(EFFECTS_KEY, effectsPercent);
    }

    private void save(String key, int value) {
        try {
            store.saveInt(key, value);
        } catch (RuntimeException ignored) {
            // The chosen volume still applies for this session.
        }
    }

    private static int normalize(int percent) {
        int clamped = Math.max(MIN_PERCENT, Math.min(MAX_PERCENT, percent));
        return Math.round(clamped / (float) STEP_PERCENT) * STEP_PERCENT;
    }
}
