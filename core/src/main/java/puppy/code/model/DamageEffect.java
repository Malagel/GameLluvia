package puppy.code.model;

import java.util.Collections;
import java.util.List;

/**
 * Removes one of the player's lives and starts the damage animation.
 */
public final class DamageEffect implements DropEffect {
    @Override
    public List<GameplayEvent> apply(Player player) {
        player.takeDamage();
        return Collections.singletonList(GameplayEvent.PLAYER_DAMAGED);
    }
}
