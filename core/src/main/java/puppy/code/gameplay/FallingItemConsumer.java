package puppy.code.gameplay;

import puppy.code.model.FallingItem;

/**
 * Receives the visible state of each active falling item, one at a time. The session keeps
 * its items private and hands out only their type and position.
 */
@FunctionalInterface
public interface FallingItemConsumer {
    void accept(FallingItem.Type type, float x, float y);
}
