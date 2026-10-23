package puppy.code.input;

/**
 * Describes the horizontal movement requested by the player, independently of the
 * device or key bindings used to express it.
 */
public interface PlayerInput {
    boolean isMovingLeft();

    boolean isMovingRight();
}
