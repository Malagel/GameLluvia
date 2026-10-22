package puppy.code.gameplay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import puppy.code.model.FallingItem;
import puppy.code.model.GameplayEvent;
import puppy.code.model.Player;

public final class GameSession {
    private final Player player;
    private final DropSource dropSource;
    private final List<FallingItemView> fallingItems;
    private final List<FallingItemView> readOnlyFallingItems;

    public GameSession() {
        this(new Player(), new DropSpawner());
    }

    GameSession(Player player, DropSource dropSource) {
        this.player = Objects.requireNonNull(player, "player");
        this.dropSource = Objects.requireNonNull(dropSource, "dropSource");
        fallingItems = new ArrayList<>();
        readOnlyFallingItems = Collections.unmodifiableList(fallingItems);
        FallingItem initialDrop = dropSource.createInitialDrop();
        if (initialDrop != null) {
            addFallingItem(initialDrop);
        }
    }

    public UpdateResult update(float delta, boolean movingLeft, boolean movingRight) {
        if (player.isHurt()) {
            return finishUpdate(UpdateResult.noEvents());
        }

        player.move(movingLeft, movingRight, delta);
        FallingItem newItem = dropSource.createDropIfDue();
        if (newItem != null) {
            addFallingItem(newItem);
        }

        List<GameplayEvent> events = new ArrayList<>();
        ListIterator<FallingItemView> iterator = fallingItems.listIterator();
        while (iterator.hasNext()) {
            FallingItem item = iterator.next().item;
            item.fall(delta);
            if (item.isBelowWorld()) {
                iterator.remove();
                continue;
            }
            if (!item.overlaps(player)) {
                continue;
            }

            events.addAll(item.applyTo(player));
            iterator.remove();
            if (player.hasNoLives()) {
                return finishUpdate(new UpdateResult(events, true));
            }
        }
        UpdateResult result = events.isEmpty()
                ? UpdateResult.noEvents()
                : new UpdateResult(events, false);
        return finishUpdate(result);
    }

    private UpdateResult finishUpdate(UpdateResult result) {
        player.advanceHurtAnimation();
        return result;
    }

    private void addFallingItem(FallingItem item) {
        fallingItems.add(new FallingItemView(item));
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

    public List<FallingItemView> getFallingItems() {
        return readOnlyFallingItems;
    }

    /**
     * Read-only projection of a session-owned falling item. One projection is
     * allocated when the item spawns and reused while it remains active.
     */
    public static final class FallingItemView {
        private final FallingItem item;

        private FallingItemView(FallingItem item) {
            this.item = Objects.requireNonNull(item, "item");
        }

        public float getX() {
            return item.getX();
        }

        public float getY() {
            return item.getY();
        }

        public FallingItem.Type getType() {
            return item.getType();
        }
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
