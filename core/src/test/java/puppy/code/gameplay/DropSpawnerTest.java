package puppy.code.gameplay;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;
import puppy.code.GameConfig;
import puppy.code.model.BadDrop;
import puppy.code.model.FallingItem;
import puppy.code.model.GoodDrop;

class DropSpawnerTest {
    @Test
    void standardProfilePreservesIntervalAndSixtyFortyWeights() {
        DropSpawnProfile profile = DropSpawner.standardProfile();

        assertEquals(100L, profile.getSpawnIntervalMillis());
        assertEquals(2, profile.getDrops().size());
        assertEquals(6, profile.getDrops().get(0).getWeight());
        assertEquals(4, profile.getDrops().get(1).getWeight());
    }

    @Test
    void weightedSelectionUsesExactDeterministicBoundaries() {
        assertInstanceOf(GoodDrop.class, createStandardDropWithSelection(0));
        assertInstanceOf(GoodDrop.class, createStandardDropWithSelection(5));
        assertInstanceOf(BadDrop.class, createStandardDropWithSelection(6));
        assertInstanceOf(BadDrop.class, createStandardDropWithSelection(9));
    }

    @Test
    void selectionSupportsOneOrSeveralRegisteredFactories() {
        AtomicInteger firstCalls = new AtomicInteger();
        AtomicInteger secondCalls = new AtomicInteger();
        AtomicInteger thirdCalls = new AtomicInteger();
        DropSpawnProfile several = DropSpawnProfile.builder()
                .spawnIntervalMillis(100L)
                .addDrop(1, (x, y) -> countedGoodDrop(firstCalls, x, y))
                .addDrop(1, (x, y) -> countedGoodDrop(secondCalls, x, y))
                .addDrop(1, (x, y) -> countedGoodDrop(thirdCalls, x, y))
                .build();

        new DropSpawner(several, new SequenceRandom(0, 2), () -> 0L).createInitialDrop();

        assertEquals(0, firstCalls.get());
        assertEquals(0, secondCalls.get());
        assertEquals(1, thirdCalls.get());

        DropSpawnProfile one = DropSpawnProfile.builder()
                .spawnIntervalMillis(100L)
                .addDrop(12, GoodDrop::new)
                .build();
        assertInstanceOf(GoodDrop.class,
                new DropSpawner(one, new SequenceRandom(0, 11), () -> 0L)
                        .createInitialDrop());
    }

    @Test
    void initialDropIsImmediateAndNextDropRequiresStrictlyMoreThanInterval() {
        AtomicLong time = new AtomicLong(1_000L);
        DropSpawner spawner = new DropSpawner(
                DropSpawner.standardProfile(),
                new SequenceRandom(12, 0, 34, 9),
                time::get);

        FallingItem initial = spawner.createInitialDrop();

        assertInstanceOf(GoodDrop.class, initial);
        assertEquals(12.0f, initial.getX());
        assertEquals(GameConfig.WORLD_HEIGHT, initial.getY());

        time.set(100_001_000L);
        assertNull(spawner.createDropIfDue());

        time.incrementAndGet();
        FallingItem next = spawner.createDropIfDue();
        assertInstanceOf(BadDrop.class, next);
        assertEquals(34.0f, next.getX());
        assertEquals(GameConfig.WORLD_HEIGHT, next.getY());
    }

    @Test
    void horizontalSpawnRangeKeepsBothInclusiveBoundaries() {
        int maximumX = (int) (GameConfig.WORLD_WIDTH - FallingItem.SIZE);
        DropSpawner spawner = new DropSpawner(
                DropSpawner.standardProfile(),
                new SequenceRandom(maximumX, 0),
                () -> 0L);

        assertEquals((float) maximumX, spawner.createInitialDrop().getX());
    }

    private FallingItem createStandardDropWithSelection(int selection) {
        DropSpawner spawner = new DropSpawner(
                DropSpawner.standardProfile(),
                new SequenceRandom(0, selection),
                () -> 0L);
        return spawner.createInitialDrop();
    }

    private FallingItem countedGoodDrop(AtomicInteger calls, float x, float y) {
        calls.incrementAndGet();
        return new GoodDrop(x, y);
    }

    private static final class SequenceRandom extends Random {
        private final Queue<Integer> values;

        private SequenceRandom(Integer... values) {
            this.values = new ArrayDeque<>(Arrays.asList(values));
        }

        @Override
        public int nextInt(int bound) {
            int value = values.remove();
            if (value < 0 || value >= bound) {
                throw new AssertionError("Scripted random value outside bound");
            }
            return value;
        }
    }
}
