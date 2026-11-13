package puppy.code.settings;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class AudioSettingsTest {
    @Test
    void defaultsToFullVolumeWhenNothingIsSaved() {
        AudioSettings settings = new AudioSettings(new InMemorySettingsStore());

        assertEquals(100, settings.getMusicPercent());
        assertEquals(100, settings.getEffectsPercent());
        assertEquals(1.0f, settings.getMusicVolume());
        assertEquals(1.0f, settings.getEffectsVolume());
    }

    @Test
    void changesVolumesInStepsAndConvertsToLibGdxRange() {
        AudioSettings settings = new AudioSettings(new InMemorySettingsStore());

        settings.decreaseMusic();
        settings.decreaseMusic();
        settings.decreaseEffects();

        assertEquals(80, settings.getMusicPercent());
        assertEquals(0.8f, settings.getMusicVolume(), 1e-6f);
        assertEquals(90, settings.getEffectsPercent());
    }

    @Test
    void keepsVolumesWithinLimits() {
        AudioSettings settings = new AudioSettings(new InMemorySettingsStore());

        settings.increaseMusic();
        for (int i = 0; i < 15; i++) {
            settings.decreaseEffects();
        }

        assertEquals(100, settings.getMusicPercent());
        assertEquals(0, settings.getEffectsPercent());
    }

    @Test
    void persistsChangesAndReloadsThem() {
        InMemorySettingsStore store = new InMemorySettingsStore();
        AudioSettings settings = new AudioSettings(store);
        settings.decreaseMusic();
        settings.decreaseEffects();
        settings.decreaseEffects();

        AudioSettings reloaded = new AudioSettings(store);

        assertEquals(90, reloaded.getMusicPercent());
        assertEquals(80, reloaded.getEffectsPercent());
    }

    @Test
    void normalizesCorruptedSavedValues() {
        InMemorySettingsStore store = new InMemorySettingsStore();
        store.saveInt(AudioSettings.MUSIC_KEY, 250);
        store.saveInt(AudioSettings.EFFECTS_KEY, 47);

        AudioSettings settings = new AudioSettings(store);

        assertEquals(100, settings.getMusicPercent());
        assertEquals(50, settings.getEffectsPercent());
    }

    @Test
    void keepsChosenVolumeWhenSavingFails() {
        AudioSettings settings = new AudioSettings(new FailingSettingsStore());

        settings.decreaseMusic();

        assertEquals(90, settings.getMusicPercent());
    }

    private static final class InMemorySettingsStore implements SettingsStore {
        private final Map<String, Integer> values = new HashMap<>();

        @Override
        public int loadInt(String key, int defaultValue) {
            return values.getOrDefault(key, defaultValue);
        }

        @Override
        public void saveInt(String key, int value) {
            values.put(key, value);
        }
    }

    private static final class FailingSettingsStore implements SettingsStore {
        @Override
        public int loadInt(String key, int defaultValue) {
            return defaultValue;
        }

        @Override
        public void saveInt(String key, int value) {
            throw new IllegalStateException("disk full");
        }
    }
}
