package sinnet.models;

import org.apache.commons.lang3.StringUtils;

import lombok.Value;

@Value
public final class Email {

  private static final Email EMPTY = new Email(null);

  private String value;

  public static Email of(String value) {
    if (StringUtils.isBlank(value))
      return EMPTY;
    return new Email(value);
  }

  public static Email empty() {
    return EMPTY;
  }
}
