package sinnet.grpc.reports.report3;

import java.util.List;

interface Models {
  
  record CustomerDetails(String name, String address, String city) { }

  record GroupDetails(String personName, List<CustomerDetails> details) { }

  record ReportRequest(List<GroupDetails> activities) { }

}
