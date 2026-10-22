package puppy.code.model;

import java.util.List;

/**
 * Defines an effect that a falling item applies when the player collects it.
 */
public interface DropEffect {
    /**
     * Applies this effect and reports the outcomes that presentation layers may react to.
     * The returned list must not be {@code null} and must not contain {@code null} values.
     */
    List<GameplayEvent> apply(Player player);
}
