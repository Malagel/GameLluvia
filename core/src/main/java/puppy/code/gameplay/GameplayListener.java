package puppy.code.gameplay;

import puppy.code.model.GameplayEvent;

/**
 * Observer notified by {@link GameSession} of each player-visible outcome as it happens.
 */
@FunctionalInterface
public interface GameplayListener {
    void onGameplayEvent(GameplayEvent event);
}
