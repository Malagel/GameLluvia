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

    /**
     * The effect that replaces this one when the rain intensifies. Effects that do not
     * change keep themselves, so only the strategies that grow need to override it.
     */
    default DropEffect intensified() {
        return this;
    }
}
