package puppy.code.gameplay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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

    int getTotalWeight() {
        return totalWeight;
    }

    /**
     * Creates the drop selected by {@code roll}, a value in {@code [0, getTotalWeight())}.
     * Each registration owns a range of rolls as wide as its weight, in registration order.
     * The profile does the selection itself because it is the only owner of its
     * registrations, which are never exposed.
     */
    FallingItem createDrop(int roll, float x, float y) {
        if (roll < 0 || roll >= totalWeight) {
            throw new IllegalArgumentException("roll must be in [0, " + totalWeight + ")");
        }
        int remaining = roll;
        for (DropRegistration drop : drops) {
            if (remaining < drop.weight) {
                return Objects.requireNonNull(drop.factory.create(x, y), "drop factory result");
            }
            remaining -= drop.weight;
        }
        throw new IllegalStateException("Profile weights did not select a drop");
    }

    long getSpawnIntervalNanos() {
        return spawnIntervalMillis * NANOS_PER_MILLISECOND;
    }

    @FunctionalInterface
    public interface DropFactory {
        FallingItem create(float x, float y);
    }

    private static final class DropRegistration {
        private final int weight;
        private final DropFactory factory;

        private DropRegistration(int weight, DropFactory factory) {
            this.weight = weight;
            this.factory = factory;
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
