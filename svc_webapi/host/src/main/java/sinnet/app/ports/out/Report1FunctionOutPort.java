package sinnet.app.ports.out;

import sinnet.report1.grpc.ReportRequests;

/**
 * Port-out interface for calling Azure Function implementation of report1.
 */
public interface Report1FunctionOutPort {

  byte[] producePack(ReportRequests request);
}
