package puppy.code.gameplay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import puppy.code.model.FallingItem;
import puppy.code.model.Player;

public final class GameSession {
    private final Player player;
    private final DropSpawner dropSpawner;
    private final List<FallingItem> fallingItems;
    private final List<FallingItem> readOnlyFallingItems;

    public GameSession() {
        player = new Player();
        dropSpawner = new DropSpawner();
        fallingItems = new ArrayList<>();
        readOnlyFallingItems = Collections.unmodifiableList(fallingItems);
        fallingItems.add(dropSpawner.createInitialDrop());
    }

    public UpdateResult update(float delta, boolean movingLeft, boolean movingRight) {
        if (player.isHurt()) {
            return UpdateResult.noEvents();
        }

        player.move(movingLeft, movingRight, delta);
        FallingItem newItem = dropSpawner.createDropIfDue();
        if (newItem != null) {
            fallingItems.add(newItem);
        }

        List<GameplayEvent> events = new ArrayList<>();
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

            item.applyTo(player);
            iterator.remove();
            if (item.getType() == FallingItem.Type.GOOD) {
                events.add(GameplayEvent.DROP_COLLECTED);
            } else {
                events.add(GameplayEvent.PLAYER_DAMAGED);
                if (player.hasNoLives()) {
                    return new UpdateResult(events, true);
                }
            }
        }
        return events.isEmpty() ? UpdateResult.noEvents() : new UpdateResult(events, false);
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

    public void advancePlayerHurtAnimation() {
        player.advanceHurtAnimation();
    }

    public List<FallingItem> getFallingItems() {
        return readOnlyFallingItems;
    }

    public enum GameplayEvent {
        DROP_COLLECTED,
        PLAYER_DAMAGED
    }

    public static final class UpdateResult {
        private static final UpdateResult NO_EVENTS = new UpdateResult(Collections.emptyList(), false);

        private final List<GameplayEvent> events;
        private final boolean gameOver;

        private UpdateResult(List<GameplayEvent> events, boolean gameOver) {
            this.events = Collections.unmodifiableList(new ArrayList<>(events));
            this.gameOver = gameOver;
        }

        private static UpdateResult noEvents() {
            return NO_EVENTS;
        }

        public List<GameplayEvent> getEvents() {
            return events;
        }

        public boolean isGameOver() {
            return gameOver;
        }
    }
}
