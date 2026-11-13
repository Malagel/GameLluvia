package puppy.code.gameplay;

import java.util.Objects;

/**
 * Director (GoF) for rain profiles. It fixes the construction sequence shared by every kind
 * of rain: the interval first, then beneficial drops, then harmful drops. Registration order
 * decides which rolls select which drop, so every profile gives beneficial drops the low
 * rolls and harmful drops the high ones.
 */
final class RainDirector {
    DropSpawnProfile construct(RainProfileBuilder builder) {
        Objects.requireNonNull(builder, "builder");
        builder.buildSpawnInterval();
        builder.buildBeneficialDrops();
        builder.buildHarmfulDrops();
        return builder.getProfile();
    }
}
