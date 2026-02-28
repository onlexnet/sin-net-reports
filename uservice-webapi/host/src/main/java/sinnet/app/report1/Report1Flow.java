package sinnet.app.report1;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import io.vavr.Tuple;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import sinnet.app.ports.in.Report1PortIn;
import sinnet.app.ports.out.UsersServicePortOut;
import sinnet.domain.models.Email;
import sinnet.grpc.common.UserToken;
import sinnet.grpc.users.SearchRequest;
import sinnet.infra.adapters.grpc.ActionsGrpcFacade;
import sinnet.infra.adapters.grpc.CustomersGrpcFacade;
import sinnet.infra.adapters.grpc.Reports1GrpcAdapter;
import sinnet.report1.grpc.ActivityDetails;
import sinnet.report1.grpc.CustomerDetails;
import sinnet.report1.grpc.ReportRequest;
import sinnet.report1.grpc.ReportRequests;
import sinnet.reports.grpc.Date;
import org.jspecify.annotations.Nullable;

/** Refactor - shouuld not be public. */
@Component
@RequiredArgsConstructor
public class Report1Flow implements Report1PortIn {

  private final ActionsGrpcFacade timeentries;
  private final CustomersGrpcFacade customersClient;
  private final Reports1GrpcAdapter reportsClient;
  private final UsersServicePortOut usersService;

  @Override
  public byte[] downloadPdfFile(UUID projectId, int year, int month) {
    var dateFrom = LocalDate.of(year, month, 1);
    var dateTo = LocalDate.of(year, month, 1).plusMonths(1).minusDays(1);
    var projectIdAsString = projectId.toString();
    var entries = getTimeentries(projectIdAsString, dateFrom, dateTo);
    var customers = getCustomers(projectIdAsString);
    var users = emailToName(projectId);
    var reportRequest = asReportRequests(entries, customers, users);
    var data = reportsClient.producePack(reportRequest);
    var result = data.getData().toByteArray();
    return result;
  }


  record TimeEntryModel(@Nullable String customerId, String what, LocalDate when, String servicemanName, int howFar, int howLong) { }

  private java.util.List<TimeEntryModel> getTimeentries(String projectId, LocalDate from, LocalDate to) {
    var projectIdTyped = UUID.fromString(projectId);
    return timeentries.searchInternal(projectIdTyped, from, to).stream()
        .map(it -> new TimeEntryModel(it.customerId(),
        it.description(),
        it.whenProvided(),
        it.servicemanName(),
        it.distance(),
        it.duration()))
        .toList();
  }

  /** Refactor: should not be public. */
  public record CustomerModel(String customerId,
                              String customerName,
                              String customerCity,
                              String customerAddress) {
  }

  private java.util.List<CustomerModel> getCustomers(String projectId) {
    // var authentication = (AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    // var primaryEmail = authentication.getPrincipal();
    var reply = customersClient.customerList(projectId, "ignored@owner", it -> new CustomerModel(
        it.getId().getEntityId(), 
        it.getValue().getCustomerName(),
        it.getValue().getCustomerCityName(),
        it.getValue().getCustomerAddress()));
    return reply;
  }

  private Function<String, String> emailToName(UUID projectId) {

    var response = usersService.search(projectId, Email.of("ignored@owner"));
    var emailToName = response.getItemsList().stream()
        .collect(Collectors.toMap(it -> it.getEmail(), it -> it.getCustomName()));

    return email -> {
      var customName = emailToName.get(email);
      return customName != null
          ? customName
          : "brak danych";
    };
  }

  /**
   * Prepares raport request based on given items and given customer.
   * It does not check if the customer is actually same as defined in items.
   */
  Option<ReportRequest> asReportRequest(io.vavr.collection.List<TimeEntryModel> items,
                                        Option<CustomerModel> maybeCustomer, Function<String, String> emailToName) {
    return items.headOption()
      .map(it -> {
        var requestBuilder = ReportRequest.newBuilder();

        if (maybeCustomer.isDefined()) {
          var customer = maybeCustomer.get();
          var customerId = customer.customerId();
          var customerName = customer.customerName();
          var customerCity = customer.customerCity();
          var customerAddress = customer.customerAddress();
          var customerBuilder = CustomerDetails.newBuilder()
              .setCustomerId(customerId.toString());
          Option.of(customerName).forEach(customerBuilder::setCustomerName);
          Option.of(customerCity).forEach(customerBuilder::setCustomerCity);
          Option.of(customerAddress).forEach(customerBuilder::setCustomerAddress);
          requestBuilder.setCustomer(customerBuilder);
        }

        items
            // let's simplify path of obtaining the value
            .forEach(item -> {
              var v = item;
              var builder = ActivityDetails.newBuilder();
              Option.of(v.what()).forEach(builder::setDescription);
              builder
                  .setHowFarInKms(v.howFar())
                  .setHowLongInMins(v.howLong());
              Option.of(v.when()).forEach(o -> builder
                  .setWhen(Date.newBuilder()
                  .setYear(v.when().getYear())
                  .setMonth(v.when().getMonthValue())
                  .setDayOfTheMonth(v.when().getDayOfMonth())));
              Option.of(item.servicemanName()).map(emailToName)
                  .forEach(builder::setWho);

              requestBuilder.addDetails(builder);
            });
        
        return requestBuilder.build();
      });
  }

  ReportRequests asReportRequests(List<TimeEntryModel> items, List<CustomerModel> customers, Function<String, String> emailToName) {
    return io.vavr.collection.List.ofAll(items)
        // lets group actions related to the same customer
        .groupBy(it -> it.customerId())
        .map((k, v) -> {
          var customerId = k;
          var customer = io.vavr.collection.List.ofAll(customers).find(it -> Objects.equals(customerId, it.customerId()));
          var newValue = asReportRequest(v, customer, emailToName);
          return Tuple.of(k, newValue);
        })
        // key is no longer needed
        .map(kv -> kv._2)
        // unpack Option value to raw value
        .flatMap(Function.identity())
        .foldLeft(ReportRequests.newBuilder(), (acc, v) -> acc.addItems(v))
        .build();
  }
   
}
