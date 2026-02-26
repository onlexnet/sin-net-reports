package sinnet.app.ports.out;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


/**
 * Port-out interface for report1 data access.
 */
public interface Report1PortOut {

  /**
   * Gets time entries for a project in a date range.
   *
   * @param projectId the project UUID
   * @param from start date
   * @param to end date
   * @return list of time entry models
   */
  List<TimeEntryModel> getTimeentries(UUID projectId, LocalDate from, LocalDate to);

  /**
   * Gets customers for a project.
   *
   * @param projectId the project UUID
   * @return list of customer models
   */
  List<CustomerModel> getCustomers(UUID projectId);

  /**
   * Gets a function mapping email to name for a project.
   *
   * @param projectId the project UUID
   * @return function mapping email to name
   */
  java.util.function.Function<String, String> emailToName(UUID projectId);

  /**
   * DTO for time entry.
   */
  record TimeEntryModel(
      String customerId,
      String what,
      LocalDate when,
      String servicemanName,
      int howFar,
      int howLong
  ) {}

  /**
   * DTO for customer.
   */
  record CustomerModel(
      String customerId,
      String customerName,
      String customerCity,
      String customerAddress
  ) {}
}