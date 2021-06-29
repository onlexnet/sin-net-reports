package sinnet.web;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.vavr.Function1;
import io.vavr.collection.Array;
import io.vavr.control.Option;
import sinnet.FutureExecutor;
import sinnet.read.ActionProjection;
import sinnet.reports.ActivityDetails;
import sinnet.reports.CustomerDetails;
import sinnet.reports.Date;
import sinnet.reports.ReportRequest;
import sinnet.reports.ReportRequests;
import sinnet.reports.ReportsGrpc;

@RestController
@RequestMapping(path = "/api/raporty")
class ReportController implements ActionProjection {

  @Autowired
  private ActionProjection.Provider projection;

  @Autowired
  private ReportsGrpc.ReportsFutureStub reportsClient;

  @Autowired
  private FutureExecutor executor;

  @RequestMapping(value = "/klienci/{projectId}/{year}/{month}", method = RequestMethod.GET, produces = "application/pdf")
  public CompletionStage<ResponseEntity<byte[]>> downloadPdfFile(@PathVariable UUID projectId,
                                                                 @PathVariable int year, @PathVariable int month) {

    var headers = new HttpHeaders();
    headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
    headers.add("Content-Disposition", "inline; filename=report " + year + "-" + month + ".zip");
    headers.add("Expires", "0");

    var dateFrom = LocalDate.of(year, month, 1);
    var dateTo = LocalDate.of(year, month, 1).plusMonths(1).minusDays(1);
    return projection
        .find(projectId, dateFrom, dateTo)
        .toCompletionStage()
        .thenCompose(it -> {
          var reportRequest = asReportRequests(it);
          var reportResult = reportsClient.producePack(reportRequest);
          return executor.asFuture(reportResult, Function1.identity());
        })
        .thenApplyAsync(it -> {
          var result = it.getData().toByteArray();
          return ResponseEntity.ok()
              .headers(headers)
              .contentLength(result.length)
              .contentType(MediaType.parseMediaType("application/octet-stream"))
              .body(result);
        });
  }

  Option<ReportRequest> asReportRequest(Array<ListItem> items) {
    return items.headOption()
      .map(it -> {
        var requestBuilder = ReportRequest.newBuilder();

        var customerId = it.getValue().getWhom();
        var customerName = it.getCustomerName();
        var customerCity = it.getCustomerCity();
        var customerAddress = it.getCustomerAddress();
        
        if (customerId != null) {
          var customerBuilder = CustomerDetails.newBuilder()
              .setCustomerId(customerId.toString());
          Option.of(customerName).forEach(customerBuilder::setCustomerName);
          Option.of(customerCity).forEach(customerBuilder::setCustomerCity);
          Option.of(customerAddress).forEach(customerBuilder::setCustomerAddress);
          requestBuilder.setCustomer(customerBuilder);
        }

        items
          // let's simplify path of obtaining the value
          .map(v -> v.getValue())
          .forEach(v -> {
            var builder = ActivityDetails.newBuilder();
            Option.of(v.getWhat()).forEach(builder::setDescription);
            builder
              .setHowFarInKms(v.getHowFar().getValue())
              .setHowLongInMins(v.getHowLong().getValue());
            Option.of(v.getWhen()).forEach(o -> builder
                .setWhen(Date.newBuilder()
                .setYear(v.getWhen().getYear())
                .setMonth(v.getWhen().getMonthValue())
                .setDayOfTheMonth(v.getWhen().getDayOfMonth())));
            Option.of(v.getWho().getValue()).forEach(builder::setWho);

            requestBuilder.addDetails(builder);
          });
        
        return requestBuilder.build();
      });
  }

  ReportRequests asReportRequests(Array<ListItem> items) {
    return items
        // lets group actions related to the same customer
        .groupBy(it -> it.getValue().getWhom())
        .mapValues(this::asReportRequest)
        // key is no longer needed
        .map(kv -> kv._2)
        // unpack Option value to raw value
        .flatMap(Function1.identity())
        .foldLeft(ReportRequests.newBuilder(), (acc, v) -> acc.addItems(v))
        .build();
  }
}
