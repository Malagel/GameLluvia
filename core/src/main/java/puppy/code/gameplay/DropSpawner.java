package puppy.code.gameplay;

import com.badlogic.gdx.utils.TimeUtils;
import java.util.Objects;
import java.util.Random;
import java.util.function.LongSupplier;
import puppy.code.GameConfig;
import puppy.code.model.FallingItem;

/**
 * Creates drops from a {@link DropSpawnProfile} at runtime: it owns timing, randomness, and
 * spawn positions. Which profiles exist is decided elsewhere, by the rain builders.
 */
public final class DropSpawner implements DropSource {
    private final DropSpawnProfile profile;
    private final Random random;
    private final LongSupplier nanoTime;
    private long lastSpawnTime;

    public DropSpawner(DropSpawnProfile profile) {
        this(profile, new Random(), TimeUtils::nanoTime);
    }

    DropSpawner(DropSpawnProfile profile, Random random, LongSupplier nanoTime) {
        this.profile = Objects.requireNonNull(profile, "profile");
        this.random = Objects.requireNonNull(random, "random");
        this.nanoTime = Objects.requireNonNull(nanoTime, "nanoTime");
    }

    @Override
    public FallingItem createInitialDrop() {
        return createDrop();
    }

    @Override
    public FallingItem createDropIfDue() {
        if (nanoTime.getAsLong() - lastSpawnTime > profile.getSpawnIntervalNanos()) {
            return createDrop();
        }
        return null;
    }

    private FallingItem createDrop() {
        int maximumX = (int) (GameConfig.WORLD_WIDTH - FallingItem.SIZE);
        float x = random.nextInt(maximumX + 1);
        int roll = random.nextInt(profile.getTotalWeight());
        lastSpawnTime = nanoTime.getAsLong();
        return profile.createDrop(roll, x, GameConfig.WORLD_HEIGHT);
    }
}
