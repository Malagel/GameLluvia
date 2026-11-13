package puppy.code.gameplay;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import puppy.code.model.FallingItem;
import puppy.code.model.Player;

/**
 * Gameplay controller for one game. Besides moving the player, spawning, and resolving
 * collisions, it decides when the calm rain turns into a storm: once the score reaches
 * {@link #STORM_SCORE}, new drops come from the storm source for the rest of the game.
 *
 * <p>The session never hands out its collections. Outcomes are pushed to a
 * {@link GameplayListener} as they happen, and active items are visited through a
 * {@link FallingItemConsumer} that receives only their type and position.</p>
 */
public final class GameSession {
    static final int STORM_SCORE = 300;

    private final Player player;
    private final DropSource calmRain;
    private final DropSource stormRain;
    private final GameplayListener listener;
    private final List<FallingItem> fallingItems = new ArrayList<>();
    private boolean stormActive;

    public GameSession(GameplayListener listener) {
        this(new Player(),
                new DropSpawner(DropSpawner.standardProfile()),
                new DropSpawner(DropSpawner.stormProfile()),
                listener);
    }

    GameSession(Player player, DropSource calmRain, DropSource stormRain,
            GameplayListener listener) {
        this.player = Objects.requireNonNull(player, "player");
        this.calmRain = Objects.requireNonNull(calmRain, "calmRain");
        this.stormRain = Objects.requireNonNull(stormRain, "stormRain");
        this.listener = Objects.requireNonNull(listener, "listener");
        FallingItem initialDrop = calmRain.createInitialDrop();
        if (initialDrop != null) {
            fallingItems.add(initialDrop);
        }
    }

    public void update(float delta, boolean movingLeft, boolean movingRight) {
        if (!player.isHurt()) {
            advanceGameplay(delta, movingLeft, movingRight);
        }
        player.advanceHurtAnimation();
    }

    private void advanceGameplay(float delta, boolean movingLeft, boolean movingRight) {
        player.move(movingLeft, movingRight, delta);
        DropSource activeRain = stormActive ? stormRain : calmRain;
        FallingItem newItem = activeRain.createDropIfDue();
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
        if (!stormActive && player.getScore() >= STORM_SCORE) {
            stormActive = true;
        }
    }

    /**
     * Visits every active item in drawing order without exposing the items themselves.
     */
    public void forEachFallingItem(FallingItemConsumer consumer) {
        Objects.requireNonNull(consumer, "consumer");
        for (int index = 0; index < fallingItems.size(); index++) {
            FallingItem item = fallingItems.get(index);
            consumer.accept(item.getType(), item.getX(), item.getY());
        }
    }

    public boolean isGameOver() {
        return player.hasNoLives();
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

    public boolean isStormActive() {
        return stormActive;
    }
}
