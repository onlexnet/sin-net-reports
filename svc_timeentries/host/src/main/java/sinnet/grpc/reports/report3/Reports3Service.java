package sinnet.grpc.reports.report3;

import org.springframework.stereotype.Component;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import sinnet.report3.grpc.ReportRequest;
import sinnet.report3.grpc.ReportsGrpc.ReportsImplBase;
import sinnet.reports.grpc.FileResponse;

@Component
@RequiredArgsConstructor
class Reports3Service extends ReportsImplBase {

  private final Report3Query query;

  @Override
  public void produce(ReportRequest request, StreamObserver<FileResponse> responseObserver) {
    query.query(request, responseObserver);
  }

}
