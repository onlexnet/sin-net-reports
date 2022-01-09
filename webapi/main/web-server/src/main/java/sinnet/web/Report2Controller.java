package sinnet.web;

import static sinnet.models.ActionDuration.add;
import static sinnet.models.Distance.add;
import static sinnet.reports.grpc.YearMonth.newBuilder;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;
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
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Array;
import io.vavr.collection.HashMap;
import io.vavr.control.Option;
import sinnet.FutureExecutor;
import sinnet.models.ActionDuration;
import sinnet.models.Distance;
import sinnet.read.ActionProjector;
import sinnet.report2.grpc.ReportRequest;
import sinnet.report2.grpc.ReportsGrpc;

@RestController
@RequestMapping(path = "/api/raporty")
class Report2Controller implements ActionProjector {

  @Autowired
  private ActionProjector.Provider projection;

  @Autowired
  private ReportsGrpc.ReportsFutureStub reportsClient;

  @Autowired
  private FutureExecutor executor;

  @RequestMapping(value = "/2/{projectId}/{year}/{month}", method = RequestMethod.GET, produces = "application/pdf")
  public CompletionStage<ResponseEntity<byte[]>> downloadPdfFile(@PathVariable UUID projectId,
                                                                 @PathVariable int year, @PathVariable int month) {

    var headers = new HttpHeaders();
    headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
    headers.add("Content-Disposition", "inline; filename=report 2 " + year + "-" + month + ".pdf");
    headers.add("Expires", "0");

    var dateFrom = LocalDate.of(year, month, 1);
    var dateTo = LocalDate.of(year, month, 1).plusMonths(1).minusDays(1);
    return projection
        .find(projectId, dateFrom, dateTo)
        .toCompletionStage()
        .thenCompose(it -> {
          var reportRequest = asReportRequest(it);
          var reportResult = reportsClient.produce(reportRequest);
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


  ReportRequest asReportRequest(Array<ListItem> items) {
    var map = items
      .map(it -> Tuple.of(
        Option.of(it.getServicemanName()).getOrElse(it.getValue().getWho().getValue()),
        YearMonth.from(it.getValue().getWhen()),
        it.getValue().getHowLong(),
        it.getValue().getHowFar()))
      .map(it -> {
        var key = Tuple.of(it._1, it._2);
        return Tuple.of(key, Tuple.of(it._3, it._4));
      })
      .foldLeft(
        HashMap.<Tuple2<String, YearMonth>, Tuple2<ActionDuration, Distance>>empty(),
        (acc, v) -> acc.put(v._1, v._2, (v1, v2) -> Tuple.of(add(v1._1, v2._1), add(v1._2, v2._2))));

      var requestBuilder = ReportRequest.newBuilder();
      map.forEach((k, v) -> requestBuilder.addDetailsBuilder()
          .setPersonName(k._1 + "")
          .setYearMonth(newBuilder().setYear(k._2.getYear()).setMonth(k._2.getMonthValue()))
          .setHowLongInMins(v._1.getValue())
          .build());
      return requestBuilder.build();
    }

}
