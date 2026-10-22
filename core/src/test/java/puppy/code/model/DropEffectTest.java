package puppy.code.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
    void strategyMayReturnMultipleEvents() {
        DropEffect effect = player -> Arrays.asList(
                GameplayEvent.DROP_COLLECTED,
                GameplayEvent.PLAYER_DAMAGED);
        FallingItem item = new GoodDrop(0.0f, 0.0f, effect);

        assertEquals(Arrays.asList(
                GameplayEvent.DROP_COLLECTED,
                GameplayEvent.PLAYER_DAMAGED), item.applyTo(new Player()));
    }

    @Test
    void strategyMayReturnNoEvents() {
        FallingItem item = new GoodDrop(0.0f, 0.0f,
                player -> Collections.emptyList());

        assertEquals(Collections.emptyList(), item.applyTo(new Player()));
    }

    @Test
    void strategyCannotReturnANullList() {
        FallingItem item = new GoodDrop(0.0f, 0.0f, player -> null);

        assertThrows(NullPointerException.class, () -> item.applyTo(new Player()));
    }

    @Test
    void strategyCannotReturnNullEvents() {
        FallingItem item = new GoodDrop(0.0f, 0.0f,
                player -> Arrays.asList(GameplayEvent.DROP_COLLECTED, null));

        assertThrows(NullPointerException.class, () -> item.applyTo(new Player()));
    }

    @Test
    void fallingItemDefensivelyCopiesMutableStrategyResults() {
        List<GameplayEvent> strategyEvents = new ArrayList<>();
        strategyEvents.add(GameplayEvent.DROP_COLLECTED);
        FallingItem item = new GoodDrop(0.0f, 0.0f, player -> strategyEvents);

        List<GameplayEvent> returnedEvents = item.applyTo(new Player());
        strategyEvents.clear();

        assertEquals(Collections.singletonList(GameplayEvent.DROP_COLLECTED), returnedEvents);
        assertThrows(UnsupportedOperationException.class,
                () -> returnedEvents.add(GameplayEvent.PLAYER_DAMAGED));
    }
}
