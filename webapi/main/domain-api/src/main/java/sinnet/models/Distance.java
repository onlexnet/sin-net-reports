package sinnet.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/** Type to keep distance between geo points. Unit - kilometers */
@EqualsAndHashCode
public final class Distance {

  private static Distance empty = new Distance(0);

  @Getter
  /**
   * Value of the distance.
   * Invariant: value >=0.
   */
  private final int value;

  private Distance(int value) {
    this.value = Math.max(value, 0);
  }

  public static Distance of(int value) {
    return new Distance(value);
  }

  public static Distance empty() {
    return empty;
  }

  @Override
  public String toString() {
    return Integer.toString(value);
  }
}
