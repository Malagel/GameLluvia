package puppy.code.model;

public final class DamageEffect implements DropEffect {
    @Override
    public GameplayEvent apply(Player player) {
        player.takeDamage();
        return GameplayEvent.PLAYER_DAMAGED;
    }
}
