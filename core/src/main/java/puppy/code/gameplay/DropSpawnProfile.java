package puppy.code.gameplay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import puppy.code.model.FallingItem;

/**
 * Immutable configuration used by {@link DropSpawner}.
 */
public final class DropSpawnProfile {
    private static final long NANOS_PER_MILLISECOND = 1_000_000L;

    private final long spawnIntervalMillis;
    private final List<DropRegistration> drops;
    private final int totalWeight;

    private DropSpawnProfile(long spawnIntervalMillis, List<DropRegistration> drops,
            int totalWeight) {
        this.spawnIntervalMillis = spawnIntervalMillis;
        this.drops = Collections.unmodifiableList(new ArrayList<>(drops));
        this.totalWeight = totalWeight;
    }

    public static Builder builder() {
        return new Builder();
    }

    public long getSpawnIntervalMillis() {
        return spawnIntervalMillis;
    }

    public List<DropRegistration> getDrops() {
        return drops;
    }

    int getTotalWeight() {
        return totalWeight;
    }

    long getSpawnIntervalNanos() {
        return spawnIntervalMillis * NANOS_PER_MILLISECOND;
    }

    @FunctionalInterface
    public interface DropFactory {
        FallingItem create(float x, float y);
    }

    public static final class DropRegistration {
        private final int weight;
        private final DropFactory factory;

        private DropRegistration(int weight, DropFactory factory) {
            this.weight = weight;
            this.factory = factory;
        }

        public int getWeight() {
            return weight;
        }

        FallingItem create(float x, float y) {
            return factory.create(x, y);
        }
    }

    public static final class Builder {
        private long spawnIntervalMillis;
        private final List<DropRegistration> drops = new ArrayList<>();

        private Builder() {
        }

        public Builder spawnIntervalMillis(long spawnIntervalMillis) {
            this.spawnIntervalMillis = spawnIntervalMillis;
            return this;
        }

        public Builder addDrop(int weight, DropFactory factory) {
            drops.add(new DropRegistration(weight, factory));
            return this;
        }

        public DropSpawnProfile build() {
            if (spawnIntervalMillis <= 0L) {
                throw new IllegalStateException("Spawn interval must be positive");
            }
            if (spawnIntervalMillis > Long.MAX_VALUE / NANOS_PER_MILLISECOND) {
                throw new IllegalStateException("Spawn interval is too large");
            }
            if (drops.isEmpty()) {
                throw new IllegalStateException("At least one drop must be registered");
            }

            int totalWeight = 0;
            for (DropRegistration drop : drops) {
                if (drop.weight <= 0) {
                    throw new IllegalStateException("Drop weights must be positive");
                }
                if (drop.factory == null) {
                    throw new IllegalStateException("Drop factories must not be null");
                }
                if (totalWeight > Integer.MAX_VALUE - drop.weight) {
                    throw new IllegalStateException("Total drop weight is too large");
                }
                totalWeight += drop.weight;
            }
            return new DropSpawnProfile(spawnIntervalMillis, drops, totalWeight);
        }
    }
}
