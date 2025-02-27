package sinnet.models;

import com.google.common.base.Objects;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** Represents human-readable name of a company or a thing. */
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public final class ValName {

  private static ValName empty = new ValName(null);

  @Getter
  private String value;

  private ValName(String value) {
    this.value = value;
  }

  /** RAW value conversion -> DomainObject. */
  public static ValName of(String value) {
    if (Objects.equal(value, "")) {
      return empty;
    }
    return new ValName(value);
  }

  public static ValName empty() {
    return empty;
  }
}
