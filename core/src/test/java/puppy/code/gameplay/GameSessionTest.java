package puppy.code.gameplay;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import puppy.code.model.BadDrop;
import puppy.code.model.DamageEffect;
import puppy.code.model.FallingItem;
import puppy.code.model.GameplayEvent;
import puppy.code.model.GoodDrop;
import puppy.code.model.Player;
import puppy.code.model.ScoreEffect;

class GameSessionTest {
    @Test
    void injectedDamageEffectProducesDamageEventRegardlessOfDropIdentity() {
        Player player = new Player();
        GameSession session = sessionWith(player,
                new GoodDrop(player.getX(), player.getY(), new DamageEffect()));

        GameSession.UpdateResult result = session.update(0.0f, false, false);

        assertEquals(2, session.getLives());
        assertEquals(0, session.getScore());
        assertEquals(GameplayEvent.PLAYER_DAMAGED, result.getEvents().get(0));
    }

    @Test
    void injectedScoreEffectProducesCollectionEventRegardlessOfDropIdentity() {
        Player player = new Player();
        GameSession session = sessionWith(player,
                new BadDrop(player.getX(), player.getY(), new ScoreEffect(25)));

        GameSession.UpdateResult result = session.update(0.0f, false, false);

        assertEquals(3, session.getLives());
        assertEquals(25, session.getScore());
        assertEquals(GameplayEvent.DROP_COLLECTED, result.getEvents().get(0));
    }

    @Test
    void gameplayUpdateAdvancesHurtAnimationAndKeepsMovementSuspended() {
        Player player = new Player();
        GameSession session = sessionWith(player,
                new BadDrop(player.getX(), player.getY()));
        float initialX = session.getPlayerX();

        session.update(0.0f, false, false);
        assertTrue(session.isPlayerHurt());

        for (int frame = 0; frame < 48; frame++) {
            session.update(1.0f, false, true);
        }
        assertTrue(session.isPlayerHurt());
        assertEquals(initialX, session.getPlayerX());

        session.update(1.0f, false, true);
        assertFalse(session.isPlayerHurt());
        assertEquals(initialX, session.getPlayerX());

        session.update(1.0f, false, true);
        assertTrue(session.getPlayerX() > initialX);
    }

    @Test
    void resultEventsCannotBeModifiedByCallers() {
        Player player = new Player();
        GameSession session = sessionWith(player,
                new GoodDrop(player.getX(), player.getY()));

        GameSession.UpdateResult result = session.update(0.0f, false, false);

        assertThrows(UnsupportedOperationException.class,
                () -> result.getEvents().add(GameplayEvent.PLAYER_DAMAGED));
    }

    private GameSession sessionWith(Player player, FallingItem initialDrop) {
        return new GameSession(player, new FixedDropSource(initialDrop));
    }

    private static final class FixedDropSource implements DropSource {
        private final FallingItem initialDrop;

        private FixedDropSource(FallingItem initialDrop) {
            this.initialDrop = initialDrop;
        }

        @Override
        public FallingItem createInitialDrop() {
            return initialDrop;
        }

        @Override
        public FallingItem createDropIfDue() {
            return null;
        }
    }
}
