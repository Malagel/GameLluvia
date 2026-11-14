package puppy.code.gameplay;

/**
 * Builder (GoF) for a kind of rain. It declares the construction steps every
 * {@link DropSpawnProfile} is assembled from; each concrete builder decides the values of
 * its own kind of rain, and {@link RainDirector} decides the order of the steps.
 */
interface RainProfileBuilder {
    void buildSpawnInterval();

    void buildBeneficialDrops();

    void buildHarmfulDrops();

    DropSpawnProfile getProfile();
}
