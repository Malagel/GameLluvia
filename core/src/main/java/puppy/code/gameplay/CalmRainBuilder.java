package puppy.code.gameplay;

import puppy.code.model.BadDrop;
import puppy.code.model.GoodDrop;

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
