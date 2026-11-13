package puppy.code.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Test;
import puppy.code.GameConfig;

class FallingItemMotionTest {
    private static final float STEP = 1.0f / 120.0f;
    private static final float TOLERANCE = 0.01f;
    private static final float MAX_X = GameConfig.WORLD_WIDTH - FallingItem.SIZE;
    private static final float PERIOD = (float) (2.0 * Math.PI / BadDrop.SWAY_ANGULAR_SPEED);

    @Test
    void beneficialDropsFallStraightDownAtTheOriginalSpeed() {
        FallingItem drop = new GoodDrop(100.0f, 300.0f);

        drop.fall(0.1f);
        drop.fall(0.3f);

        assertEquals(100.0f, drop.getX());
        assertEquals(300.0f - 225.0f * 0.4f, drop.getY(), TOLERANCE);
    }

    @Test
    void harmfulDropsSwayAroundTheirSpawnColumnWhileFallingAtTheSameSpeed() {
        FallingItem drop = new BadDrop(100.0f, 300.0f);

        float elapsed = advance(drop, PERIOD / 4.0f);
        assertEquals(100.0f + BadDrop.SWAY_AMPLITUDE, drop.getX(), TOLERANCE);
        assertEquals(300.0f - 225.0f * elapsed, drop.getY(), 0.1f);

        advance(drop, PERIOD / 2.0f);
        assertEquals(100.0f - BadDrop.SWAY_AMPLITUDE, drop.getX(), TOLERANCE);

        advance(drop, PERIOD / 4.0f);
        assertEquals(100.0f, drop.getX(), TOLERANCE);
    }

    @Test
    void swayDoesNotDependOnFrameDuration() {
        FallingItem smoothFrames = new BadDrop(100.0f, 300.0f);
        FallingItem oneLongFrame = new BadDrop(100.0f, 300.0f);

        advance(smoothFrames, 0.3f);
        oneLongFrame.fall(0.3f);

        assertEquals(oneLongFrame.getX(), smoothFrames.getX(), TOLERANCE);
    }

    @Test
    void swayingDropsNeverLeaveTheWorldHorizontally() {
        FallingItem atRightEdge = new BadDrop(MAX_X, 300.0f);
        FallingItem atLeftEdge = new BadDrop(0.0f, 300.0f);

        for (float time = 0.0f; time < PERIOD; time += STEP) {
            atRightEdge.fall(STEP);
            atLeftEdge.fall(STEP);
            assertTrue(atRightEdge.getX() >= 0.0f && atRightEdge.getX() <= MAX_X);
            assertTrue(atLeftEdge.getX() >= 0.0f && atLeftEdge.getX() <= MAX_X);
        }
    }

    @Test
    void fallIsAFinalTemplateAndMotionIsItsOnlyHook() throws NoSuchMethodException {
        assertTrue(Modifier.isFinal(
                FallingItem.class.getMethod("fall", float.class).getModifiers()));
        assertTrue(Modifier.isProtected(FallingItem.class
                .getDeclaredMethod("horizontalDrift", float.class).getModifiers()));
    }

    /** Advances in regular frames plus one shorter final frame, covering exactly duration. */
    private static float advance(FallingItem item, float duration) {
        int fullSteps = (int) (duration / STEP);
        for (int step = 0; step < fullSteps; step++) {
            item.fall(STEP);
        }
        item.fall(duration - fullSteps * STEP);
        return duration;
    }
}
