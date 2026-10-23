package puppy.code.input;

import com.badlogic.gdx.Input;
import java.util.Objects;

/**
 * Keyboard-based {@link PlayerInput}. Each direction can be triggered by any of its
 * bound keys, so several control schemes can be active at the same time.
 */
public final class KeyboardPlayerInput implements PlayerInput {
    private static final int[] STANDARD_LEFT_KEYS = {Input.Keys.LEFT, Input.Keys.A};
    private static final int[] STANDARD_RIGHT_KEYS = {Input.Keys.RIGHT, Input.Keys.D};

    private final Input input;
    private final int[] leftKeys;
    private final int[] rightKeys;

    public KeyboardPlayerInput(Input input, int[] leftKeys, int[] rightKeys) {
        this.input = Objects.requireNonNull(input, "input");
        this.leftKeys = Objects.requireNonNull(leftKeys, "leftKeys").clone();
        this.rightKeys = Objects.requireNonNull(rightKeys, "rightKeys").clone();
    }

    /**
     * Creates the game's default controls: the arrow keys and the A/D keys.
     */
    public static KeyboardPlayerInput standard(Input input) {
        return new KeyboardPlayerInput(input, STANDARD_LEFT_KEYS, STANDARD_RIGHT_KEYS);
    }

    @Override
    public boolean isMovingLeft() {
        return isAnyPressed(leftKeys);
    }

    @Override
    public boolean isMovingRight() {
        return isAnyPressed(rightKeys);
    }

    private boolean isAnyPressed(int[] keys) {
        for (int key : keys) {
            if (input.isKeyPressed(key)) {
                return true;
            }
        }
        return false;
    }
}
