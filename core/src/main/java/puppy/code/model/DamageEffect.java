package puppy.code.model;

/**
 * Removes one of the player's lives and starts the damage animation.
 */
public final class DamageEffect implements DropEffect {
    @Override
    public GameplayEvent apply(Player player) {
        player.takeDamage();
        return GameplayEvent.PLAYER_DAMAGED;
    }
}
