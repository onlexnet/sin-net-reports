package sinnet.ws;

import static sinnet.reports.grpc.YearMonth.newBuilder;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import sinnet.gql.api.CommonMapper;
import sinnet.ports.timeentries.ActionsGrpcFacade;
import sinnet.report2.grpc.ReportRequest;
import sinnet.report2.grpc.ReportsGrpc.ReportsBlockingStub;
import sinnet.reports.grpc.UserToken;
import sinnet.web.AuthenticationToken;

@RestController
@RequestMapping("/api/raporty")
@RequiredArgsConstructor
class Report2Controller {

  private final ActionsGrpcFacade timeentries;
  private final ReportsBlockingStub reportsClient;
  private final CommonMapper commonMapper;


  @GetMapping("/2/{projectId}")
  public ResponseEntity<byte[]> downloadPdfFile(@PathVariable UUID projectId,
      @RequestParam("yearFrom") int yearFrom, @RequestParam("monthFrom") int monthFrom,
      @RequestParam("yearTo") int yearTo, @RequestParam("monthTo") int monthTo) {

    var authentication = (AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    var primaryEmail = authentication.getPrincipal();

    var dateFrom = LocalDate.of(yearFrom, monthFrom, 1);
    var dateTo = LocalDate.of(yearTo, monthTo, 1).plusMonths(1).minusDays(1);

    var entries = getTimeentries(projectId, dateFrom, dateTo);
    var reportRequest = asReportRequest(entries, primaryEmail, projectId.toString());
    var data = reportsClient.produce(reportRequest);
    var result = data.toByteArray();
    return Response.asResponseEntity(result, "report " + yearFrom + "-" + monthFrom + ".pdf");
  }

  // TODO move such aggregation logic close to data service
  ReportRequest asReportRequest(List<TimeEntryModel> javaItems, String primaryEmail, String projectId) {
    var items = io.vavr.collection.List.ofAll(javaItems);
    var map = items
        .map(it -> Tuple.of(it.getServicemanName(), YearMonth.from(it.getWhen()), it.getHowLong(), it.getHowFar()))
        .map(it -> {
          var key = Tuple.of(it._1, it._2);
          return Tuple.of(key, Tuple.of(it._3, it._4));
        })
        .foldLeft(
          HashMap.<Tuple2<String, YearMonth>, Tuple2<Integer, Integer>>empty(),
          (acc, v) -> acc.put(v._1, v._2, (v1, v2) -> Tuple.of(v1._1 + v2._1, v1._2 + v2._2)));

    var requestBuilder = ReportRequest.newBuilder();
    map.forEach((k, v) -> requestBuilder.addDetailsBuilder()
        .setPersonName(k._1 + "")
        .setYearMonth(newBuilder().setYear(k._2.getYear()).setMonth(k._2.getMonthValue()))
        .setHowLongInMins(v._1)
        .setHowFarInKms(v._2)
        .build());
    requestBuilder.setUserToken(UserToken.newBuilder().setRequestorEmail(primaryEmail));
    requestBuilder.setProjectId(projectId);
    return requestBuilder.build();
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

  private List<TimeEntryModel> getTimeentries(UUID projectId, LocalDate from, LocalDate to) {
    var data = timeentries.searchInternal(projectId, from, to).stream()
        .map(it -> new TimeEntryModel(it.getCustomerId(),
        it.getDescription(),
        commonMapper.fromGrpc(it.getWhenProvided()),
        it.getServicemanName(),
        it.getDistance(),
        it.getDuration()))
        .toList();
    return data;

  }

}
