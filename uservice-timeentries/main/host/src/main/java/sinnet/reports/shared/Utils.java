package sinnet.reports.shared;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import io.vavr.control.Option;
import lombok.experimental.UtilityClass;

/** TBD. */
@UtilityClass
public class Utils {

  private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

  /** TBD. */
  public static String maybeDateAsString(Option<LocalDate> maybeDate) {
    return maybeDate.map(it -> it.format(timeFormatter)).getOrElse("-");
  }
}
