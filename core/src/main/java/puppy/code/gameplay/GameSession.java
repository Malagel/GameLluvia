package puppy.code.gameplay;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import puppy.code.model.FallingItem;
import puppy.code.model.Player;

/**
 * Gameplay controller for one game. Besides moving the player, spawning, and resolving
 * collisions, it keeps the current {@link Weather}: new drops come from the rain source of
 * that phase, so reaching the storm score switches the game to the storm source, and the
 * drops already falling change strategy at runtime to their intensified effect.
 *
 * <p>The session never hands out its collections. Outcomes are pushed to a
 * {@link GameplayListener} as they happen, and active items are visited through a
 * {@link FallingItemConsumer} that receives only their appearance and position.</p>
 */
public final class GameSession {
    private final Player player;
    private final Map<Weather, DropSource> rainByWeather;
    private final GameplayListener listener;
    private final List<FallingItem> fallingItems = new ArrayList<>();
    private Weather weather = Weather.CALM;

    public GameSession(GameplayListener listener) {
        this(new Player(), standardRain(), listener);
    }

    GameSession(Player player, Map<Weather, DropSource> rainByWeather,
            GameplayListener listener) {
        this.player = Objects.requireNonNull(player, "player");
        this.rainByWeather = new EnumMap<>(Objects.requireNonNull(rainByWeather, "rainByWeather"));
        for (Weather phase : Weather.values()) {
            Objects.requireNonNull(this.rainByWeather.get(phase), "rain source for " + phase);
        }
        this.listener = Objects.requireNonNull(listener, "listener");
        FallingItem initialDrop = this.rainByWeather.get(weather).createInitialDrop();
        if (initialDrop != null) {
            fallingItems.add(initialDrop);
        }
    }

    private static Map<Weather, DropSource> standardRain() {
        Map<Weather, DropSource> rain = new EnumMap<>(Weather.class);
        rain.put(Weather.CALM, new DropSpawner(DropSpawner.standardProfile()));
        rain.put(Weather.STORM, new DropSpawner(DropSpawner.stormProfile()));
        return rain;
    }

    public void update(float delta, boolean movingLeft, boolean movingRight) {
        if (!player.isHurt()) {
            advanceGameplay(delta, movingLeft, movingRight);
        }
        player.advanceHurtAnimation();
    }

    private void advanceGameplay(float delta, boolean movingLeft, boolean movingRight) {
        player.move(movingLeft, movingRight, delta);
        FallingItem newItem = rainByWeather.get(weather).createDropIfDue();
        if (newItem != null) {
            fallingItems.add(newItem);
        }

        ListIterator<FallingItem> iterator = fallingItems.listIterator();
        while (iterator.hasNext()) {
            FallingItem item = iterator.next();
            item.fall(delta);
            if (item.isBelowWorld()) {
                iterator.remove();
                continue;
            }
            if (!item.overlaps(player)) {
                continue;
            }

            listener.onGameplayEvent(item.applyTo(player));
            iterator.remove();
            if (player.hasNoLives()) {
                return;
            }
        }
        updateWeather();
    }

    /**
     * When the rain intensifies, the drops already falling switch to their stronger
     * strategy at once, so every drop on screen matches the new phase.
     */
    private void updateWeather() {
        Weather reached = Weather.forScore(player.getScore());
        if (reached == weather) {
            return;
        }
        weather = reached;
        for (int index = 0; index < fallingItems.size(); index++) {
            fallingItems.get(index).intensify();
        }
    }

    /**
     * Visits every active item in drawing order without exposing the items themselves.
     */
    public void forEachFallingItem(FallingItemConsumer consumer) {
        Objects.requireNonNull(consumer, "consumer");
        for (int index = 0; index < fallingItems.size(); index++) {
            FallingItem item = fallingItems.get(index);
            consumer.accept(item.getAppearance(), item.getX(), item.getY());
        }
    }

    public boolean isGameOver() {
        return player.hasNoLives();
    }

    public Weather getWeather() {
        return weather;
    }

    public int getScore() {
        return player.getScore();
    }

    public int getLives() {
        return player.getLives();
    }

    public float getPlayerX() {
        return player.getX();
    }

    public float getPlayerY() {
        return player.getY();
    }

    public boolean isPlayerHurt() {
        return player.isHurt();
    }
}
