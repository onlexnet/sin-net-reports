package sinnet.app.ports.out;

import lombok.experimental.UtilityClass;

/** Shared naming convention for report1 ZIP downloads, used by both the legacy and function-backed flows. */
@UtilityClass
public class Report1FileNaming {

  /** Builds the download filename for a monthly report ZIP, e.g. {@code "report 2024-3.zip"}. */
  public static String zipFileName(int year, int month) {
    return "report " + year + "-" + month + ".zip";
  }
}
