package sinnet.grpc.reports.common;

import java.util.Arrays;

/**
 * List of all columns available for activities.
 * In some logic we can merge some of them (using e.g. sum of two values for one column)
 * but finally we should use all columns so that all tables create using such columns will have
 * similar location of columns.
 */
public record TableColumn(int width) {

  /** Doxme. */
  public static TableColumn of(TableColumn... that) {
    var extraSize = Arrays.stream(that).mapToInt(it -> it.width()).sum();
    return new TableColumn(extraSize);
  }
}
