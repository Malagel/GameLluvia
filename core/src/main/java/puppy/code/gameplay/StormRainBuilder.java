package puppy.code.gameplay;

import puppy.code.model.BadDrop;
import puppy.code.model.DropEffect;
import puppy.code.model.GoodDrop;
import puppy.code.model.ScoreEffect;

/**
 * Concrete builder for the storm: denser rain (one drop every 75 ms) with the same 60/40
 * proportion, in which beneficial drops are created with a strategy worth double points.
 */
final class StormRainBuilder implements RainProfileBuilder {
    static final int BENEFICIAL_POINTS = 20;

    private final DropSpawnProfile.Builder profile = DropSpawnProfile.builder();

    @Override
    public void buildSpawnInterval() {
        profile.spawnIntervalMillis(75L);
    }

    @Override
    public void buildBeneficialDrops() {
        DropEffect doubleReward = new ScoreEffect(BENEFICIAL_POINTS);
        profile.addDrop(6, (x, y) -> new GoodDrop(x, y, doubleReward));
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
