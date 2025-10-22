package sinnet.ws;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.vavr.Tuple;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import lombok.RequiredArgsConstructor;
import sinnet.gql.utils.PropsBuilder;
import sinnet.grpc.customers.ListReply;
import sinnet.grpc.customers.ListRequest;
import sinnet.ports.timeentries.CustomersGrpcFacade;
import sinnet.report3.grpc.CustomerDetails;
import sinnet.report3.grpc.GroupDetails;
import sinnet.report3.grpc.ReportRequest;
import sinnet.report3.grpc.ReportsGrpc.ReportsBlockingStub;

@RestController
@RequestMapping("/api/raporty")
@RequiredArgsConstructor
class Report3Controller {

  private final CustomersGrpcFacade customera;
  private final ReportsBlockingStub reportsClient;
  
  @GetMapping(value = "/3/{projectId}", produces = "application/zip")
  public ResponseEntity<byte[]> downloadPdfFile(@PathVariable UUID projectId) {

    var projectIdAsString = projectId.toString();
    var listRequest = ListRequest.newBuilder()
        .setProjectId(projectIdAsString)
        .build();
    var customerList = customera.list(listRequest);
    var reportRequest = asReportRequest(customerList);
    var reportData = reportsClient.produce(reportRequest);
    var result = reportData.getData().toByteArray();

    return Response.asResponseEntity(result, "report-3.pdf");
        
  }

  // TODO move aggregation closer to data service
  ReportRequest asReportRequest(ListReply reply) {
    return List.ofAll(reply.getCustomersList())
      .filter(it -> StringUtils.isNotBlank(it.getValue().getOperatorEmail()))
      .map(it -> Tuple.of(
          it.getValue().getOperatorEmail(),
          PropsBuilder.build(CustomerDetails.newBuilder())
            .set(b -> b::setName, it.getValue().getCustomerName())
            .set(b -> b::setAddress, it.getValue().getCustomerAddress())
            .set(b -> b::setCity, it.getValue().getCustomerCityName())
            .done().build()))
      .foldLeft(
        HashMap.<String, List<CustomerDetails>>empty(),
        (acc, v) -> acc.put(v._1, List.of(v._2), (o1, o2) -> o1.appendAll(o2)))
      .mapKeys(it -> GroupDetails.newBuilder().setPersonName(it))
      .toList()
      .map(it -> it._1.addAllDetails(it._2))
      .foldLeft(ReportRequest.newBuilder(), (acc, v) -> acc.addDetails(v))
      .build();
  }

}
