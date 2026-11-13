package puppy.code.gameplay;

import puppy.code.model.DropAppearance;

/**
 * Receives the visible state of each active falling item, one at a time. The session keeps
 * its items private and hands out only their appearance and position.
 */
@FunctionalInterface
public interface FallingItemConsumer {
    void accept(DropAppearance appearance, float x, float y);
}
