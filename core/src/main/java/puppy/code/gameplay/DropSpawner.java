package puppy.code.gameplay;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;
import puppy.code.model.BadDrop;
import puppy.code.model.FallingItem;
import puppy.code.model.GoodDrop;

public final class DropSpawner {
    private static final float WORLD_WIDTH = 800.0f;
    private static final float SPAWN_Y = 480.0f;
    private static final long SPAWN_INTERVAL_NANOS = 100_000_000L;

    private long lastSpawnTime;

    public FallingItem createInitialDrop() {
        return createDrop();
    }

    public FallingItem createDropIfDue() {
        if (TimeUtils.nanoTime() - lastSpawnTime > SPAWN_INTERVAL_NANOS) {
            return createDrop();
        }
        return null;
    }

    private FallingItem createDrop() {
        float x = MathUtils.random(0, (int) (WORLD_WIDTH - FallingItem.SIZE));
        lastSpawnTime = TimeUtils.nanoTime();
        if (MathUtils.random(1, 10) < 5) {
            return new BadDrop(x, SPAWN_Y);
        }
        return new GoodDrop(x, SPAWN_Y);
    }
}
