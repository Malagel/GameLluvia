package puppy.code.gameplay;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import puppy.code.model.BadDrop;
import puppy.code.model.FallingItem;
import puppy.code.model.GameplayEvent;
import puppy.code.model.GoodDrop;
import puppy.code.model.Player;

class RainBuilderTest {
    private final RainDirector director = new RainDirector();

    @Test
    void directorRunsTheConstructionStepsInAFixedOrder() {
        RecordingBuilder builder = new RecordingBuilder();

        DropSpawnProfile profile = director.construct(builder);

        assertEquals(Arrays.asList("interval", "beneficial", "harmful", "result"), builder.steps);
        assertSame(builder.result, profile);
    }

    @Test
    void directorRequiresABuilder() {
        assertThrows(NullPointerException.class, () -> director.construct(null));
    }

    @Test
    void calmRainKeepsTheOriginalIntervalAndSixtyFortyWeights() {
        assertSixtyFortyProfile(director.construct(new CalmRainBuilder()), 100L);
    }

    @Test
    void stormIsDenserAndKeepsTheSixtyFortyWeights() {
        assertSixtyFortyProfile(director.construct(new StormRainBuilder()), 75L);
    }

    @Test
    void stormSelectsADifferentStrategyForTheSameBeneficialDropKind() {
        FallingItem calmDrop = director.construct(new CalmRainBuilder()).createDrop(0, 0f, 0f);
        FallingItem stormDrop = director.construct(new StormRainBuilder()).createDrop(0, 0f, 0f);
        Player calmPlayer = new Player();
        Player stormPlayer = new Player();

        assertEquals(calmDrop.getClass(), stormDrop.getClass());
        assertSame(calmDrop.getAppearance(), stormDrop.getAppearance());
        assertEquals(GameplayEvent.DROP_COLLECTED, stormDrop.applyTo(stormPlayer));
        calmDrop.applyTo(calmPlayer);

        assertEquals(10, calmPlayer.getScore());
        assertEquals(StormRainBuilder.BENEFICIAL_POINTS, stormPlayer.getScore());
    }

    @Test
    void stormKeepsHarmfulDropsHarmful() {
        Player player = new Player();

        FallingItem stormDrop = director.construct(new StormRainBuilder()).createDrop(9, 0f, 0f);

        assertInstanceOf(BadDrop.class, stormDrop);
        assertEquals(GameplayEvent.PLAYER_DAMAGED, stormDrop.applyTo(player));
        assertEquals(2, player.getLives());
        assertEquals(0, player.getScore());
    }

    /** Six beneficial rolls (0-5) followed by four harmful ones (6-9). */
    private static void assertSixtyFortyProfile(DropSpawnProfile profile, long intervalMillis) {
        assertEquals(intervalMillis, profile.getSpawnIntervalMillis());
        assertEquals(10, profile.getTotalWeight());
        assertInstanceOf(GoodDrop.class, profile.createDrop(0, 0.0f, 0.0f));
        assertInstanceOf(GoodDrop.class, profile.createDrop(5, 0.0f, 0.0f));
        assertInstanceOf(BadDrop.class, profile.createDrop(6, 0.0f, 0.0f));
        assertInstanceOf(BadDrop.class, profile.createDrop(9, 0.0f, 0.0f));
    }

    private static final class RecordingBuilder implements RainProfileBuilder {
        private final List<String> steps = new ArrayList<>();
        private final DropSpawnProfile result = DropSpawnProfile.builder()
                .spawnIntervalMillis(1L)
                .addDrop(1, GoodDrop::new)
                .build();

        @Override
        public void buildSpawnInterval() {
            steps.add("interval");
        }

        @Override
        public void buildBeneficialDrops() {
            steps.add("beneficial");
        }

        @Override
        public void buildHarmfulDrops() {
            steps.add("harmful");
        }

        @Override
        public DropSpawnProfile getProfile() {
            steps.add("result");
            return result;
        }
    }
}
