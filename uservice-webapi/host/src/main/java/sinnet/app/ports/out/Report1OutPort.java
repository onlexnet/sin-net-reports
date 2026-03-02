package sinnet.app.ports.out;

/**
 * Port-out interface for report1 data access.
 */
public interface Report1OutPort {

  public sinnet.reports.grpc.Response produce(sinnet.report1.grpc.ReportRequest request);

  public sinnet.reports.grpc.Response producePack(sinnet.report1.grpc.ReportRequests request);
}