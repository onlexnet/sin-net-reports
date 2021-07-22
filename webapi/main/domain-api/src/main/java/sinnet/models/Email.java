package sinnet.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@AllArgsConstructor
@Value
@Jacksonized
@Builder(toBuilder = true)
public final class Email {

  private static final Email EMPTY = new Email(null);

  private String value;

  public static Email of(String value) {
    return new Email(value);
  }

  public static Email empty() {
    return EMPTY;
  }
}
