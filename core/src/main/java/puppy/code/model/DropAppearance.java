package puppy.code.model;

import java.util.Objects;

/**
 * Visual identity of one kind of falling item. Each {@link FallingItem} subclass declares
 * its own constant, so the parent class never lists its subclasses and a new kind of drop
 * needs no change to existing domain classes. Instances compare by identity.
 */
public final class DropAppearance {
    private final String name;

    public DropAppearance(String name) {
        this.name = Objects.requireNonNull(name, "name");
    }

    @Override
    public String toString() {
        return name;
    }
}
