package sinnet.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/** Represents human-readable name of a company or a thing. */
@EqualsAndHashCode
@ToString
public final class Name {

    private static Name empty = new Name(null);

    @Getter
    private final String value;

    private Name(String value) {
        this.value = value;
    }

    public static Name of(String value) {
        return new Name(value);
    }

    public static Name empty() {
        return empty;
    }
}
