package puppy.code.gameplay;

import puppy.code.model.FallingItem;

interface DropSource {
    FallingItem createInitialDrop();

    FallingItem createDropIfDue();
}
