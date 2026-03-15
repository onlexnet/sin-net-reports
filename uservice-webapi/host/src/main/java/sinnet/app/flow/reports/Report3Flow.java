package sinnet.app.flow.reports;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import io.vavr.Tuple;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import lombok.RequiredArgsConstructor;
import sinnet.app.flow.request.CustomerListQuery;
import sinnet.app.flow.request.CustomerListResult;
import sinnet.domain.models.UserToken;
import sinnet.app.ports.in.Report3PortIn;
import sinnet.app.ports.out.CustomersPortOut;
import sinnet.gql.utils.PropsBuilder;
import sinnet.report3.grpc.CustomerDetails;
import sinnet.report3.grpc.GroupDetails;
import sinnet.report3.grpc.ReportRequest;
import sinnet.report3.grpc.ReportsGrpc.ReportsBlockingStub;

@Component
@RequiredArgsConstructor
class Report3Flow implements Report3PortIn {

  private static final String REPORT_REQUESTOR_EMAIL = "ignored@owner";

  private final CustomersPortOut customera;
  private final ReportsBlockingStub reportsClient;
  
  @Override
  public byte[] downloadPdfFile(UUID projectId) {
    var userToken = new UserToken(projectId, REPORT_REQUESTOR_EMAIL);
    var customerList = customera.list(new CustomerListQuery(userToken));
    var reportRequest = asReportRequest(customerList);
    var reportData = reportsClient.produce(reportRequest);
    return reportData.getData().toByteArray();
  }

  // TODO move aggregation closer to data service
  ReportRequest asReportRequest(CustomerListResult reply) {
    return List.ofAll(reply.customers())
      .filter(it -> StringUtils.isNotBlank(it.value().entry().operatorEmail()))
      .map(it -> Tuple.of(
          it.value().entry().operatorEmail(),
          PropsBuilder.build(CustomerDetails.newBuilder())
            .set(b -> b::setName, it.value().entry().customerName())
            .set(b -> b::setAddress, it.value().entry().customerAddress())
            .set(b -> b::setCity, it.value().entry().customerCityName())
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
