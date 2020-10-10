package sinnet.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public final class Email {

    private static Email empty = new Email(null);

    @Getter
    private final String value;

    private Email(String value) {
        this.value = value;
    }

    public static Email of(String value) {
        return new Email(value);
    }

    public static Email empty() {
        return empty;
    }
}
