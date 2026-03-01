package sinnet.infra.adapters.grpc;

import org.springframework.stereotype.Component;

import sinnet.app.ports.out.Report1PortOut;
import sinnet.report1.grpc.ReportsGrpc.ReportsBlockingStub;

/** Mockable equivalent of {@link ReportsBlockingStub}. */
@Component
public class Reports1GrpcAdapter implements Report1PortOut {

  private final ReportsBlockingStub stub;

  public Reports1GrpcAdapter(ReportsBlockingStub stub) {
    this.stub = stub;
  }

  public sinnet.reports.grpc.Response produce(sinnet.report1.grpc.ReportRequest request) {
    return stub.produce(request);
  }

  public sinnet.reports.grpc.Response producePack(sinnet.report1.grpc.ReportRequests request) {
    return stub.producePack(request);
  }


}
