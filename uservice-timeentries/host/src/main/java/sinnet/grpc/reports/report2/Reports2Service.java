package sinnet.grpc.reports.report2;

import org.springframework.stereotype.Component;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import sinnet.report2.grpc.ReportRequest;
import sinnet.report2.grpc.ReportsGrpc.ReportsImplBase;
import sinnet.reports.grpc.Response;

@Component
@RequiredArgsConstructor
class Reports2Service extends ReportsImplBase {

  private final Report2Query query;

  @Override
  public void produce(ReportRequest request, StreamObserver<Response> responseObserver) {
    query.query(request, responseObserver);
  }
}
