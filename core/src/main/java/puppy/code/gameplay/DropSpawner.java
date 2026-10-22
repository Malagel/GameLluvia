package puppy.code.gameplay;

import com.badlogic.gdx.utils.TimeUtils;
import java.util.Objects;
import java.util.Random;
import java.util.function.LongSupplier;
import puppy.code.GameConfig;
import puppy.code.model.BadDrop;
import puppy.code.model.FallingItem;
import puppy.code.model.GoodDrop;

public final class DropSpawner implements DropSource {
    private final DropSpawnProfile profile;
    private final Random random;
    private final LongSupplier nanoTime;
    private long lastSpawnTime;

    public DropSpawner() {
        this(standardProfile());
    }

    public DropSpawner(DropSpawnProfile profile) {
        this(profile, new Random(), TimeUtils::nanoTime);
    }

    DropSpawner(DropSpawnProfile profile, Random random, LongSupplier nanoTime) {
        this.profile = Objects.requireNonNull(profile, "profile");
        this.random = Objects.requireNonNull(random, "random");
        this.nanoTime = Objects.requireNonNull(nanoTime, "nanoTime");
    }

    public static DropSpawnProfile standardProfile() {
        return DropSpawnProfile.builder()
                .spawnIntervalMillis(100L)
                .addDrop(6, GoodDrop::new)
                .addDrop(4, BadDrop::new)
                .build();
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
        int selectedWeight = random.nextInt(profile.getTotalWeight());
        lastSpawnTime = nanoTime.getAsLong();
        for (DropSpawnProfile.DropRegistration drop : profile.getDrops()) {
            if (selectedWeight < drop.getWeight()) {
                return Objects.requireNonNull(
                        drop.create(x, GameConfig.WORLD_HEIGHT),
                        "drop factory result");
            }
            selectedWeight -= drop.getWeight();
        }
        throw new IllegalStateException("Profile weights did not select a drop");
    }
}
