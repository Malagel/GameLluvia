package puppy.code.input;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.badlogic.gdx.Input;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KeyboardPlayerInputTest {
    private Input input;
    private PlayerInput playerInput;

    @BeforeEach
    void setUp() {
        input = mock(Input.class);
        playerInput = KeyboardPlayerInput.standard(input);
    }

    @Test
    void noPressedKeyMeansNoMovement() {
        assertFalse(playerInput.isMovingLeft());
        assertFalse(playerInput.isMovingRight());
    }

    @Test
    void arrowKeysMoveInTheirDirection() {
        press(Input.Keys.LEFT);
        assertTrue(playerInput.isMovingLeft());
        assertFalse(playerInput.isMovingRight());

        release(Input.Keys.LEFT);
        press(Input.Keys.RIGHT);
        assertFalse(playerInput.isMovingLeft());
        assertTrue(playerInput.isMovingRight());
    }

    @Test
    void aAndDKeysMoveInTheirDirection() {
        press(Input.Keys.A);
        assertTrue(playerInput.isMovingLeft());
        assertFalse(playerInput.isMovingRight());

        release(Input.Keys.A);
        press(Input.Keys.D);
        assertFalse(playerInput.isMovingLeft());
        assertTrue(playerInput.isMovingRight());
    }

    @Test
    void keysOfDifferentSchemesCanBeCombined() {
        press(Input.Keys.A);
        press(Input.Keys.RIGHT);

        assertTrue(playerInput.isMovingLeft());
        assertTrue(playerInput.isMovingRight());
    }

    @Test
    void unrelatedKeysAreIgnored() {
        press(Input.Keys.W);
        press(Input.Keys.S);
        press(Input.Keys.UP);

        assertFalse(playerInput.isMovingLeft());
        assertFalse(playerInput.isMovingRight());
    }

    @Test
    void customBindingsReplaceTheStandardOnes() {
        PlayerInput custom = new KeyboardPlayerInput(
                input, new int[] {Input.Keys.J}, new int[] {Input.Keys.L});

        press(Input.Keys.A);
        assertFalse(custom.isMovingLeft());

        press(Input.Keys.J);
        press(Input.Keys.L);
        assertTrue(custom.isMovingLeft());
        assertTrue(custom.isMovingRight());
    }

    @Test
    void bindingsAreCopiedDefensively() {
        int[] left = {Input.Keys.J};
        PlayerInput custom = new KeyboardPlayerInput(input, left, new int[] {Input.Keys.L});
        left[0] = Input.Keys.K;

        press(Input.Keys.K);
        assertFalse(custom.isMovingLeft());

        press(Input.Keys.J);
        assertTrue(custom.isMovingLeft());
    }

    @Test
    void constructorRejectsNullArguments() {
        int[] keys = {Input.Keys.J};

        assertThrows(NullPointerException.class, () -> new KeyboardPlayerInput(null, keys, keys));
        assertThrows(NullPointerException.class, () -> new KeyboardPlayerInput(input, null, keys));
        assertThrows(NullPointerException.class, () -> new KeyboardPlayerInput(input, keys, null));
    }

    private void press(int key) {
        when(input.isKeyPressed(key)).thenReturn(true);
    }

    private void release(int key) {
        when(input.isKeyPressed(key)).thenReturn(false);
    }
}
