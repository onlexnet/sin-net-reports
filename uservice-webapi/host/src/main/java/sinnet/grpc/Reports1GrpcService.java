package sinnet.grpc;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import sinnet.report1.grpc.ReportsGrpc.ReportsBlockingStub;

/** Mockable equivalent of {@link ReportsBlockingStub}. */
@Component
@RequiredArgsConstructor
public class Reports1GrpcService {

  private interface Reports1Service {

    public sinnet.reports.grpc.Response produce(sinnet.report1.grpc.ReportRequest request);

    public sinnet.reports.grpc.Response producePack(sinnet.report1.grpc.ReportRequests request);

  }

  @Delegate(types = Reports1Service.class)
  private final ReportsBlockingStub stub;

}
