package sinnet.grpc.reports.report2;

import java.time.YearMonth;
import java.util.List;

import sinnet.models.ActionDuration;
import sinnet.models.Distance;

interface Models {
  record ActivityDetails(YearMonth period, String personName, Distance kilometers, ActionDuration minutes) { }

  record ReportRequest(List<ActivityDetails> activities, String requestorEmail) { }
}
