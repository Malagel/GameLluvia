package puppy.code.model;

/**
 * Defines an effect that a falling item applies when the player collects it.
 */
public interface DropEffect {
    void apply(Player player);
}
