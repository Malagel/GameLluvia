package puppy.code.gameplay;

import puppy.code.model.BadDrop;
import puppy.code.model.GoodDrop;

/**
 * Concrete builder for calm rain: one drop every 100 ms, 60% beneficial drops worth their
 * default 10 points and 40% harmful drops.
 */
final class CalmRainBuilder implements RainProfileBuilder {
    private final DropSpawnProfile.Builder profile = DropSpawnProfile.builder();

    @Override
    public void buildSpawnInterval() {
        profile.spawnIntervalMillis(100L);
    }

    @Override
    public void buildBeneficialDrops() {
        profile.addDrop(6, GoodDrop::new);
    }

    @Override
    public void buildHarmfulDrops() {
        profile.addDrop(4, BadDrop::new);
    }

    @Override
    public DropSpawnProfile getProfile() {
        return profile.build();
    }
}
