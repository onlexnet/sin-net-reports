package sinnet.ws;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.vavr.Function1;
import io.vavr.Tuple;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import sinnet.gql.api.CommonMapper;
import sinnet.gql.api.CustomerMapper;
import sinnet.grpc.ActionsGrpcFacade;
import sinnet.grpc.CustomersGrpcFacade;
import sinnet.grpc.Reports1GrpcAdapter;
import sinnet.report1.grpc.ActivityDetails;
import sinnet.report1.grpc.CustomerDetails;
import sinnet.report1.grpc.ReportRequest;
import sinnet.report1.grpc.ReportRequests;
import sinnet.reports.grpc.Date;
import sinnet.web.AuthenticationToken;

@RestController
@RequestMapping("/api/raporty")
@RequiredArgsConstructor
class Report1Controller implements CustomerMapper {

  private final ActionsGrpcFacade timeentries;
  private final CustomersGrpcFacade customersClient;
  private final Reports1GrpcAdapter reportsClient;

  @GetMapping("/klienci/{projectId}/{year}/{month}")
  public ResponseEntity<byte[]> downloadPdfFile(@PathVariable UUID projectId, @PathVariable int year, @PathVariable int month) {

    var dateFrom = LocalDate.of(year, month, 1);
    var dateTo = LocalDate.of(year, month, 1).plusMonths(1).minusDays(1);
    var projectIdAsString = projectId.toString();
    var entries = getTimeentries(projectIdAsString, dateFrom, dateTo);
    var customers = getCustomers(projectIdAsString);
    var reportRequest = asReportRequests(entries, customers);
    var data = reportsClient.producePack(reportRequest);
    var result = data.toByteArray();
    return Response.asResponseEntity(result, "report " + year + "-" + month + ".zip");
  }

  @Value 
  class TimeEntryModel {
    String customerId;
    String what;
    LocalDate when;
    String servicemanName;
    int howFar;
    int howLong;
  }

  private java.util.List<TimeEntryModel> getTimeentries(String projectId, LocalDate from, LocalDate to) {
    var projectIdTyped = UUID.fromString(projectId);
    return timeentries.searchInternal(projectIdTyped, from, to).stream()
        .map(it -> new TimeEntryModel(it.getCustomerId(),
        it.getDescription(),
        CommonMapper.fromGrpc(it.getWhenProvided()),
        it.getServicemanName(),
        it.getDistance(),
        it.getDuration()))
        .toList();
  }

  @Value
  class CustomerModel {
    String customerId;
    String customerName;
    String customerCity;
    String customerAddress;
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

  /**
   * Prepares raport request based on given items and given customer.
   * It does not check if the customer is actually same as defined in items.
   */
  Option<ReportRequest> asReportRequest(io.vavr.collection.List<TimeEntryModel> items, Option<CustomerModel> maybeCustomer) {
    return items.headOption()
      .map(it -> {
        var requestBuilder = ReportRequest.newBuilder();

        if (maybeCustomer.isDefined()) {
          var customer = maybeCustomer.get();
          var customerId = customer.getCustomerId();
          var customerName = customer.getCustomerName();
          var customerCity = customer.getCustomerCity();
          var customerAddress = customer.getCustomerAddress();
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
              Option.of(v.getWhat()).forEach(builder::setDescription);
              builder
                  .setHowFarInKms(v.getHowFar())
                  .setHowLongInMins(v.getHowLong());
              Option.of(v.getWhen()).forEach(o -> builder
                  .setWhen(Date.newBuilder()
                  .setYear(v.getWhen().getYear())
                  .setMonth(v.getWhen().getMonthValue())
                  .setDayOfTheMonth(v.getWhen().getDayOfMonth())));
              // Option.of(v.getWho())
              //     .map(o -> o.split("@")[0])
              //     .forEach(builder::setWho);
              Option.of(item.getServicemanName())
                  .forEach(builder::setWho);

              requestBuilder.addDetails(builder);
            });
        
        return requestBuilder.build();
      });
  }

  ReportRequests asReportRequests(List<TimeEntryModel> items, List<CustomerModel> customers) {
    return io.vavr.collection.List.ofAll(items)
        // lets group actions related to the same customer
        .groupBy(it -> it.getCustomerId())
        .map((k, v) -> {
          var customerId = k;
          var customer = io.vavr.collection.List.ofAll(customers).find(it -> Objects.equals(customerId, it.getCustomerId()));
          var newValue = asReportRequest(v, customer);
          return Tuple.of(k, newValue);
        })
        // key is no longer needed
        .map(kv -> kv._2)
        // unpack Option value to raw value
        .flatMap(Function1.identity())
        .foldLeft(ReportRequests.newBuilder(), (acc, v) -> acc.addItems(v))
        .build();
  }
}
