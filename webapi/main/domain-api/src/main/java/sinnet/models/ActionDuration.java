package sinnet.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;

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

  @Override
  public String toString() {
    final int minutesPerHour = 60;
    var minutes = value % minutesPerHour;
    var hours = value / minutesPerHour;
    final int minumuValueWithDoubleDigits = 10;
    var minutesPrefix = minutes < minumuValueWithDoubleDigits ? ":0" : ":";
    var minutesAsText = minutesPrefix + minutes;
    return hours + minutesAsText;
  }
}
