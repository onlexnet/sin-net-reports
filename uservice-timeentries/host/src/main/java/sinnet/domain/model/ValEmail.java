package sinnet.domain.model;

import org.apache.commons.lang3.StringUtils;

import lombok.Value;
import lombok.experimental.Accessors;

/**
 * TBD.
 */
@Value
@Accessors(fluent = true)
public final class ValEmail {

  private static final ValEmail EMPTY = new ValEmail(null);

  private String value;

  /**
   * TBD.
   */
  public static ValEmail of(String value) {

    if (StringUtils.isBlank(value)) {
      return EMPTY;
    }
    return new ValEmail(value);
  }

  public static ValEmail empty() {
    return EMPTY;
  }
}
