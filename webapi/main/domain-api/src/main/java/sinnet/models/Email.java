package sinnet.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public final class Email {

    private static final Email EMPTY = new Email(null);

    @Getter
    private final String value;

    private Email(String value) {
        this.value = value;
    }

    public static Email of(String value) {
        return new Email(value);
    }

    public static Email empty() {
        return EMPTY;
    }
}
