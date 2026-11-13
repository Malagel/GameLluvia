package puppy.code.model;

/**
 * Defines an effect that a falling item applies when the player collects it.
 */
public interface DropEffect {
    /**
     * Applies this effect and reports the outcome that presentation layers may react to.
     * The returned event must not be {@code null}.
     */
    GameplayEvent apply(Player player);
}
