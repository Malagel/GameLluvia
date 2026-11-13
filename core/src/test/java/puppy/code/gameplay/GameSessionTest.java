package puppy.code.gameplay;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import puppy.code.model.BadDrop;
import puppy.code.model.DamageEffect;
import puppy.code.model.FallingItem;
import puppy.code.model.GameplayEvent;
import puppy.code.model.GoodDrop;
import puppy.code.model.Player;
import puppy.code.model.ScoreEffect;

class GameSessionTest {
    private static final int STORM_SCORE = Weather.STORM.getStartingScore();

    private final List<GameplayEvent> events = new ArrayList<>();

    @Test
    void injectedDamageEffectProducesDamageEventRegardlessOfDropIdentity() {
        Player player = new Player();
        GameSession session = sessionWith(player,
                new GoodDrop(player.getX(), player.getY(), new DamageEffect()));

        session.update(0.0f, false, false);

        assertEquals(2, session.getLives());
        assertEquals(0, session.getScore());
        assertEquals(Collections.singletonList(GameplayEvent.PLAYER_DAMAGED), events);
    }

    @Test
    void injectedScoreEffectProducesCollectionEventRegardlessOfDropIdentity() {
        Player player = new Player();
        GameSession session = sessionWith(player,
                new BadDrop(player.getX(), player.getY(), new ScoreEffect(25)));

        session.update(0.0f, false, false);

        assertEquals(3, session.getLives());
        assertEquals(25, session.getScore());
        assertEquals(Collections.singletonList(GameplayEvent.DROP_COLLECTED), events);
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
    void framesWithoutCollisionsNotifyNothing() {
        Player player = new Player();
        GameSession session = sessionWith(player, new BadDrop(0.0f, 300.0f));

        session.update(0.016f, false, false);

        assertTrue(events.isEmpty());
        assertFalse(session.isGameOver());
        assertEquals(3, session.getLives());
    }

    @Test
    void losingTheLastLifeEndsTheGameAndStopsResolvingCollisions() {
        Player player = new Player();
        player.takeDamage();
        player.takeDamage();
        while (player.isHurt()) {
            player.advanceHurtAnimation();
        }
        GameSession session = new GameSession(player,
                rain(new SequenceDropSource(
                                new BadDrop(player.getX(), player.getY()),
                                new GoodDrop(player.getX(), player.getY())),
                        new SequenceDropSource()),
                events::add);

        session.update(0.0f, false, false);

        assertTrue(session.isGameOver());
        assertEquals(Collections.singletonList(GameplayEvent.PLAYER_DAMAGED), events);
        assertEquals(0, session.getScore());
    }

    @Test
    void activeItemsAreVisitedWithTheirTypeAndCurrentPosition() {
        Player player = new Player();
        GameSession session = sessionWith(player, new GoodDrop(0.0f, 100.0f));
        List<String> visited = new ArrayList<>();

        session.update(0.1f, false, false);
        session.forEachFallingItem((type, x, y) -> visited.add(type + "@" + x + "," + y));

        assertEquals(Collections.singletonList(GoodDrop.APPEARANCE + "@0.0,77.5"), visited);
    }

    @Test
    void visitingItemsRequiresAConsumer() {
        GameSession session = sessionWith(new Player(), null);

        assertThrows(NullPointerException.class, () -> session.forEachFallingItem(null));
    }

    @Test
    void reachingTheStormScoreSwitchesNewDropsToTheStormSource() {
        Player player = new Player();
        player.addScore(STORM_SCORE - 10);
        SequenceDropSource calm =
                new SequenceDropSource(new GoodDrop(player.getX(), player.getY()));
        SequenceDropSource storm = new SequenceDropSource();
        GameSession session = new GameSession(player, rain(calm, storm), events::add);

        session.update(0.0f, false, false);

        assertEquals(STORM_SCORE, session.getScore());
        assertEquals(Weather.STORM, session.getWeather());
        assertEquals(1, calm.dueRequests);
        assertEquals(0, storm.dueRequests);

        session.update(0.0f, false, false);
        session.update(0.0f, false, false);

        assertEquals(Weather.STORM, session.getWeather());
        assertEquals(1, calm.dueRequests);
        assertEquals(2, storm.dueRequests);
    }

    @Test
    void dropsAlreadyFallingChangeStrategyWhenTheStormStarts() {
        Player player = new Player();
        player.addScore(STORM_SCORE - 10);
        GoodDrop triggersStorm = new GoodDrop(player.getX(), player.getY());
        GoodDrop stillFalling = new GoodDrop(player.getX(), player.getY() + 60.0f);
        GameSession session = new GameSession(player,
                rain(new SequenceDropSource(triggersStorm, stillFalling), new SequenceDropSource()),
                events::add);

        session.update(0.0f, false, false);
        assertEquals(STORM_SCORE, session.getScore());
        assertEquals(Weather.STORM, session.getWeather());

        session.update(0.1f, false, false);

        assertEquals(STORM_SCORE + 20, session.getScore());
        assertEquals(Arrays.asList(GameplayEvent.DROP_COLLECTED, GameplayEvent.DROP_COLLECTED),
                events);
    }

    @Test
    void theRainStaysCalmBelowTheStormScore() {
        Player player = new Player();
        player.addScore(STORM_SCORE - 20);
        SequenceDropSource calm =
                new SequenceDropSource(new GoodDrop(player.getX(), player.getY()));
        SequenceDropSource storm = new SequenceDropSource();
        GameSession session = new GameSession(player, rain(calm, storm), events::add);

        session.update(0.0f, false, false);
        session.update(0.0f, false, false);

        assertEquals(STORM_SCORE - 10, session.getScore());
        assertEquals(Weather.CALM, session.getWeather());
        assertEquals(2, calm.dueRequests);
        assertEquals(0, storm.dueRequests);
    }

    @Test
    void sessionRequiresAListener() {
        assertThrows(NullPointerException.class, () -> new GameSession(null));
    }

    @Test
    void sessionRequiresARainSourceForEveryWeather() {
        Map<Weather, DropSource> calmOnly = new EnumMap<>(Weather.class);
        calmOnly.put(Weather.CALM, new SequenceDropSource());

        assertThrows(NullPointerException.class,
                () -> new GameSession(new Player(), calmOnly, events::add));
    }

    private GameSession sessionWith(Player player, FallingItem initialDrop) {
        SequenceDropSource calm = initialDrop == null
                ? new SequenceDropSource()
                : new SequenceDropSource(initialDrop);
        return new GameSession(player, rain(calm, new SequenceDropSource()), events::add);
    }

    private static Map<Weather, DropSource> rain(DropSource calm, DropSource storm) {
        Map<Weather, DropSource> rain = new EnumMap<>(Weather.class);
        rain.put(Weather.CALM, calm);
        rain.put(Weather.STORM, storm);
        return rain;
    }

    /** Supplies the first item at start-up and the rest one per frame, then nothing. */
    private static final class SequenceDropSource implements DropSource {
        private final List<FallingItem> items;
        private int dueRequests;

        private SequenceDropSource(FallingItem... items) {
            this.items = new ArrayList<>(Arrays.asList(items));
        }

        @Override
        public FallingItem createInitialDrop() {
            return items.isEmpty() ? null : items.remove(0);
        }

        @Override
        public FallingItem createDropIfDue() {
            dueRequests++;
            return items.isEmpty() ? null : items.remove(0);
        }
    }
}
