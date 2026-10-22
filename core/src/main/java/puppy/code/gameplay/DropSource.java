package puppy.code.gameplay;

import puppy.code.model.FallingItem;

/**
 * Supplies falling items to a game session.
 */
interface DropSource {
    FallingItem createInitialDrop();

    FallingItem createDropIfDue();
}
