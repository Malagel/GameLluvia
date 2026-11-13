package puppy.code.gameplay;

import com.badlogic.gdx.utils.TimeUtils;
import java.util.Objects;
import java.util.Random;
import java.util.function.LongSupplier;
import puppy.code.GameConfig;
import puppy.code.model.BadDrop;
import puppy.code.model.DropEffect;
import puppy.code.model.FallingItem;
import puppy.code.model.GoodDrop;
import puppy.code.model.ScoreEffect;

public final class DropSpawner implements DropSource {
    static final int STORM_DROP_POINTS = 20;

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

    /** Calm rain: one drop every 100 ms, 60% beneficial (10 points) and 40% harmful. */
    public static DropSpawnProfile standardProfile() {
        return DropSpawnProfile.builder()
                .spawnIntervalMillis(100L)
                .addDrop(6, GoodDrop::new)
                .addDrop(4, BadDrop::new)
                .build();
    }

    /**
     * Storm: denser rain (one drop every 75 ms) with the same 60/40 proportion, in which
     * beneficial drops are created with a non-default strategy worth double points. The
     * strategy is chosen here, when each drop is created, and stays fixed for its lifetime.
     */
    public static DropSpawnProfile stormProfile() {
        DropEffect doubleReward = new ScoreEffect(STORM_DROP_POINTS);
        return DropSpawnProfile.builder()
                .spawnIntervalMillis(75L)
                .addDrop(6, (x, y) -> new GoodDrop(x, y, doubleReward))
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
