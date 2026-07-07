package sinnet.grpc.reports.report1;

import org.springframework.stereotype.Component;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import sinnet.report1.grpc.ReportRequest;
import sinnet.report1.grpc.ReportRequests;
import sinnet.report1.grpc.ReportsGrpc.ReportsImplBase;
import sinnet.reports.grpc.Response;

@Component
@RequiredArgsConstructor
class Reports1Service extends ReportsImplBase {

  private final Report1QueryPack projectsPack;
  private final Report1Query projects;

  @Override
  public void produce(ReportRequest request, StreamObserver<Response> responseObserver) {
    projects.query(request, responseObserver);
  }

  @Override
  public void producePack(ReportRequests request, StreamObserver<Response> responseObserver) {
    projectsPack.query(request, responseObserver);
  }

}
