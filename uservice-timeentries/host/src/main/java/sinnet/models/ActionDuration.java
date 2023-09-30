package sinnet.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.val;

/**
 * TBD.
 */
@EqualsAndHashCode
public final class ActionDuration {

  private static final ActionDuration EMPTY = new ActionDuration(0);

  @Getter
  private int value;

  private ActionDuration(int valueInMins) {
    var valueCandidate = valueInMins;
    if (valueCandidate < 0) {
      valueCandidate = 0;
    }
    this.value = valueCandidate;
  }

  public static ActionDuration of(int value) {
    return new ActionDuration(value);
  }

  public static ActionDuration empty() {
    return EMPTY;
  }

  public static ActionDuration add(ActionDuration v1, ActionDuration v2) {
    return ActionDuration.of(v1.value + v2.value);
  }

  /** Default, company-wide approved formatting for time. */
  public String asString() {

    val minutesPerHour = 60;
    val minutes = value % minutesPerHour;
    val hours = value / minutesPerHour;

    val minumuValueWithDoubleDigits = 10;
    val minutesPrefix = (minutes < minumuValueWithDoubleDigits) ? ":0" : ":";
    var minutesAsText = minutesPrefix + minutes;

    return hours + minutesAsText;
  }

}
