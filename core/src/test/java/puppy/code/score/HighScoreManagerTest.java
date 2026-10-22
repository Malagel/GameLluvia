package puppy.code.score;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HighScoreManagerTest {
    @BeforeEach
    void resetSingletonBeforeTest() {
        HighScoreManager.shutdown();
    }

    @AfterEach
    void resetSingletonAfterTest() {
        HighScoreManager.shutdown();
    }

    @Test
    void singletonKeepsOneControlledInstance() {
        InMemoryScoreStore firstStore = new InMemoryScoreStore("");

        HighScoreManager first = HighScoreManager.initialize(firstStore);
        HighScoreManager second = HighScoreManager.initialize(new InMemoryScoreStore("999"));

        assertSame(first, second);
        assertSame(first, HighScoreManager.getInstance());
    }

    @Test
    void getInstanceRequiresInitialization() {
        assertThrows(IllegalStateException.class, HighScoreManager::getInstance);
    }

    @Test
    void ordersScoresKeepsEqualResultsAndLimitsRankingToFive() {
        HighScoreManager manager = HighScoreManager.initialize(new InMemoryScoreStore(""));

        manager.recordScore(1L, 30);
        manager.recordScore(2L, 80);
        manager.recordScore(3L, 50);
        manager.recordScore(4L, 80);
        manager.recordScore(5L, 10);
        manager.recordScore(6L, 60);

        assertEquals(Arrays.asList(80, 80, 60, 50, 30), manager.getScores());
        assertThrows(UnsupportedOperationException.class, () -> manager.getScores().add(100));
    }

    @Test
    void reloadsPersistedRankingAcrossApplicationLifecycles() {
        InMemoryScoreStore store = new InMemoryScoreStore("");
        HighScoreManager manager = HighScoreManager.initialize(store);
        manager.recordScore(1L, 25);
        manager.recordScore(2L, 75);

        HighScoreManager.shutdown();
        HighScoreManager reloaded = HighScoreManager.initialize(store);

        assertEquals(Arrays.asList(75, 25), reloaded.getScores());
    }

    @Test
    void rewritesStorageWithOnlyTheFiveHighestScores() {
        InMemoryScoreStore store = new InMemoryScoreStore("90,80,70,60,50,40,30");

        HighScoreManager manager = HighScoreManager.initialize(store);
        manager.recordScore(1L, 10);

        assertEquals(Arrays.asList(90, 80, 70, 60, 50), manager.getScores());
        assertEquals("90,80,70,60,50", store.data);
    }

    @Test
    void handlesMissingEmptyAndMalformedSavedData() {
        InMemoryScoreStore missingStore = new InMemoryScoreStore(null);
        assertEquals(Collections.emptyList(), HighScoreManager.initialize(missingStore).getScores());

        HighScoreManager.shutdown();
        InMemoryScoreStore emptyStore = new InMemoryScoreStore("   ");
        assertEquals(Collections.emptyList(), HighScoreManager.initialize(emptyStore).getScores());

        HighScoreManager.shutdown();
        InMemoryScoreStore malformedStore =
                new InMemoryScoreStore(" 100,not-a-score,-20,50,,25 ");
        assertEquals(Arrays.asList(100, 50, 25),
                HighScoreManager.initialize(malformedStore).getScores());
        assertEquals("100,50,25", malformedStore.data);
    }

    @Test
    void recordsEachCompletedGameOnlyOnce() {
        InMemoryScoreStore store = new InMemoryScoreStore("");
        HighScoreManager manager = HighScoreManager.initialize(store);

        assertTrue(manager.recordScore(42L, 100));
        assertFalse(manager.recordScore(42L, 100));

        assertEquals(Collections.singletonList(100), manager.getScores());
        assertEquals(1, store.saveCount);
    }

    @Test
    void saveFailureKeepsTheAcceptedRankingAndDoesNotEscape() {
        FailingScoreStore store = new FailingScoreStore("90,70", 1);
        HighScoreManager manager = HighScoreManager.initialize(store);

        assertTrue(manager.recordScore(1L, 80));

        assertEquals(Arrays.asList(90, 80, 70), manager.getScores());
        assertTrue(manager.hasPendingSave());
        assertEquals("90,70", store.data);
    }

    @Test
    void pendingSaveCanBeRetriedWithoutDuplicatingTheCompletion() {
        FailingScoreStore store = new FailingScoreStore("", 1);
        HighScoreManager manager = HighScoreManager.initialize(store);

        assertTrue(manager.recordScore(1L, 100));
        assertTrue(manager.hasPendingSave());

        assertTrue(manager.retryPendingSave());
        assertFalse(manager.hasPendingSave());
        assertFalse(manager.recordScore(1L, 100));

        assertEquals(Collections.singletonList(100), manager.getScores());
        assertEquals("100", store.data);
        assertEquals(2, store.saveCount);
    }

    private static final class InMemoryScoreStore implements ScoreStore {
        private String data;
        private int saveCount;

        private InMemoryScoreStore(String initialData) {
            data = initialData;
        }

        @Override
        public String load() {
            return data;
        }

        @Override
        public void save(String serializedScores) {
            data = serializedScores;
            saveCount++;
        }
    }

    private static final class FailingScoreStore implements ScoreStore {
        private String data;
        private int failuresRemaining;
        private int saveCount;

        private FailingScoreStore(String initialData, int failuresRemaining) {
            data = initialData;
            this.failuresRemaining = failuresRemaining;
        }

        @Override
        public String load() {
            return data;
        }

        @Override
        public void save(String serializedScores) {
            saveCount++;
            if (failuresRemaining > 0) {
                failuresRemaining--;
                throw new IllegalStateException("temporary save failure");
            }
            data = serializedScores;
        }
    }
}
