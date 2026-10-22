package puppy.code.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class DropEffectTest {
    @Test
    void goodDropAwardsTenPointsByDefault() {
        Player player = new Player();

        new GoodDrop(0.0f, 0.0f).applyTo(player);

        assertEquals(10, player.getScore());
    }

    @Test
    void badDropRemovesOneLifeByDefault() {
        Player player = new Player();

        new BadDrop(0.0f, 0.0f).applyTo(player);

        assertEquals(2, player.getLives());
    }

    @Test
    void fallingItemDelegatesToAnInjectedEffect() {
        Player player = new Player();
        FallingItem item = new GoodDrop(0.0f, 0.0f, new ScoreEffect(25));

        item.applyTo(player);

        assertEquals(25, player.getScore());
    }
}
