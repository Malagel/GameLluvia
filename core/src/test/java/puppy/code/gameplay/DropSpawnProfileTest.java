package puppy.code.gameplay;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import puppy.code.model.BadDrop;
import puppy.code.model.GoodDrop;

class DropSpawnProfileTest {
    @Test
    void builderCreatesProfileWithConfiguredIntervalAndWeights() {
        DropSpawnProfile profile = DropSpawnProfile.builder()
                .spawnIntervalMillis(250L)
                .addDrop(7, GoodDrop::new)
                .addDrop(3, BadDrop::new)
                .build();

        assertEquals(250L, profile.getSpawnIntervalMillis());
        assertEquals(2, profile.getDrops().size());
        assertEquals(7, profile.getDrops().get(0).getWeight());
        assertEquals(3, profile.getDrops().get(1).getWeight());
        assertEquals(10, profile.getTotalWeight());
    }

    @Test
    void completedProfileDoesNotExposeMutableRegistrations() {
        DropSpawnProfile profile = validBuilder().build();

        assertThrows(UnsupportedOperationException.class,
                () -> profile.getDrops().add(profile.getDrops().get(0)));
    }

    @Test
    void profilesRemainIndependentWhenBuilderIsReused() {
        DropSpawnProfile.Builder builder = validBuilder();
        DropSpawnProfile first = builder.build();

        builder.spawnIntervalMillis(300L).addDrop(4, BadDrop::new);
        DropSpawnProfile second = builder.build();

        assertEquals(100L, first.getSpawnIntervalMillis());
        assertEquals(1, first.getDrops().size());
        assertEquals(300L, second.getSpawnIntervalMillis());
        assertEquals(2, second.getDrops().size());
    }

    @Test
    void buildRejectsMissingOrInvalidInterval() {
        assertThrows(IllegalStateException.class,
                () -> DropSpawnProfile.builder().addDrop(1, GoodDrop::new).build());
        assertThrows(IllegalStateException.class,
                () -> DropSpawnProfile.builder().spawnIntervalMillis(-1L)
                        .addDrop(1, GoodDrop::new).build());
        assertThrows(IllegalStateException.class,
                () -> DropSpawnProfile.builder().spawnIntervalMillis(Long.MAX_VALUE)
                        .addDrop(1, GoodDrop::new).build());
    }

    @Test
    void buildRejectsMissingDropsInvalidWeightsAndNullFactories() {
        assertThrows(IllegalStateException.class,
                () -> DropSpawnProfile.builder().spawnIntervalMillis(100L).build());
        assertThrows(IllegalStateException.class,
                () -> DropSpawnProfile.builder().spawnIntervalMillis(100L)
                        .addDrop(0, GoodDrop::new).build());
        assertThrows(IllegalStateException.class,
                () -> DropSpawnProfile.builder().spawnIntervalMillis(100L)
                        .addDrop(-1, GoodDrop::new).build());
        assertThrows(IllegalStateException.class,
                () -> DropSpawnProfile.builder().spawnIntervalMillis(100L)
                        .addDrop(1, null).build());
    }

    @Test
    void buildRejectsOverflowingTotalWeight() {
        assertThrows(IllegalStateException.class,
                () -> DropSpawnProfile.builder().spawnIntervalMillis(100L)
                        .addDrop(Integer.MAX_VALUE, GoodDrop::new)
                        .addDrop(1, BadDrop::new)
                        .build());
    }

    private DropSpawnProfile.Builder validBuilder() {
        return DropSpawnProfile.builder()
                .spawnIntervalMillis(100L)
                .addDrop(1, GoodDrop::new);
    }
}
