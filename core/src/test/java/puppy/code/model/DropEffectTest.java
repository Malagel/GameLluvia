package puppy.code.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class DropEffectTest {
    @Test
    void goodDropAwardsTenPointsByDefault() {
        Player player = new Player();

        FallingItem item = new GoodDrop(0.0f, 0.0f);

        assertEquals(GameplayEvent.DROP_COLLECTED, item.applyTo(player).get(0));
        assertEquals(10, player.getScore());
    }

    @Test
    void badDropRemovesOneLifeByDefault() {
        Player player = new Player();

        FallingItem item = new BadDrop(0.0f, 0.0f);

        assertEquals(GameplayEvent.PLAYER_DAMAGED, item.applyTo(player).get(0));
        assertEquals(2, player.getLives());
    }

    @Test
    void fallingItemDelegatesToAnInjectedEffect() {
        Player player = new Player();
        FallingItem item = new GoodDrop(0.0f, 0.0f, new ScoreEffect(25));

        assertEquals(GameplayEvent.DROP_COLLECTED, item.applyTo(player).get(0));

        assertEquals(25, player.getScore());
    }

    @Test
    void identityDoesNotDetermineInjectedEffectOrReportedEvent() {
        Player player = new Player();
        FallingItem visuallyGoodItem = new GoodDrop(0.0f, 0.0f, new DamageEffect());

        assertEquals(FallingItem.Type.GOOD, visuallyGoodItem.getType());
        assertEquals(GameplayEvent.PLAYER_DAMAGED, visuallyGoodItem.applyTo(player).get(0));
        assertEquals(2, player.getLives());
        assertEquals(0, player.getScore());
    }

    @Test
    void scoreEffectProtectsThePositivePointsInvariant() {
        assertThrows(IllegalArgumentException.class, () -> new ScoreEffect(0));
        assertThrows(IllegalArgumentException.class, () -> new ScoreEffect(-1));
    }
}
