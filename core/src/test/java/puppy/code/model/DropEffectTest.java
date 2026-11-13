package puppy.code.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class DropEffectTest {
    @Test
    void goodDropAwardsTenPointsByDefault() {
        Player player = new Player();

        FallingItem item = new GoodDrop(0.0f, 0.0f);

        assertEquals(GameplayEvent.DROP_COLLECTED, item.applyTo(player));
        assertEquals(10, player.getScore());
    }

    @Test
    void badDropRemovesOneLifeByDefault() {
        Player player = new Player();

        FallingItem item = new BadDrop(0.0f, 0.0f);

        assertEquals(GameplayEvent.PLAYER_DAMAGED, item.applyTo(player));
        assertEquals(2, player.getLives());
    }

    @Test
    void fallingItemDelegatesToAnInjectedEffect() {
        Player player = new Player();
        FallingItem item = new GoodDrop(0.0f, 0.0f, new ScoreEffect(25));

        assertEquals(GameplayEvent.DROP_COLLECTED, item.applyTo(player));

        assertEquals(25, player.getScore());
    }

    @Test
    void identityDoesNotDetermineInjectedEffectOrReportedEvent() {
        Player player = new Player();
        FallingItem visuallyGoodItem = new GoodDrop(0.0f, 0.0f, new DamageEffect());

        assertSame(GoodDrop.APPEARANCE, visuallyGoodItem.getAppearance());
        assertEquals(GameplayEvent.PLAYER_DAMAGED, visuallyGoodItem.applyTo(player));
        assertEquals(2, player.getLives());
        assertEquals(0, player.getScore());
    }

    @Test
    void scoreEffectProtectsThePositivePointsInvariant() {
        assertThrows(IllegalArgumentException.class, () -> new ScoreEffect(0));
        assertThrows(IllegalArgumentException.class, () -> new ScoreEffect(-1));
    }

    @Test
    void positiveScoreAwardsSaturateAtTheIntegerLimit() {
        Player player = new Player();
        player.addScore(Integer.MAX_VALUE - 5);

        new ScoreEffect(10).apply(player);

        assertEquals(Integer.MAX_VALUE, player.getScore());
    }

    @Test
    void playerRejectsNonPositiveScoreAwardsAtItsStateBoundary() {
        Player player = new Player();

        assertThrows(IllegalArgumentException.class, () -> player.addScore(0));
        assertThrows(IllegalArgumentException.class, () -> player.addScore(-1));
        assertEquals(0, player.getScore());
    }

    @Test
    void customStrategyDecidesTheReportedEvent() {
        Player player = new Player();
        FallingItem item = new GoodDrop(0.0f, 0.0f, target -> GameplayEvent.PLAYER_DAMAGED);

        assertEquals(GameplayEvent.PLAYER_DAMAGED, item.applyTo(player));
    }

    @Test
    void strategyCannotReturnANullEvent() {
        FallingItem item = new GoodDrop(0.0f, 0.0f, player -> null);

        assertThrows(NullPointerException.class, () -> item.applyTo(new Player()));
    }

    @Test
    void applyingRequiresAPlayer() {
        FallingItem item = new GoodDrop(0.0f, 0.0f);

        assertThrows(NullPointerException.class, () -> item.applyTo(null));
    }
}
