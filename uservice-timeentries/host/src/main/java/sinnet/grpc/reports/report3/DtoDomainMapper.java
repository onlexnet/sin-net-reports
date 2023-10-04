package sinnet.grpc.reports.report3;

import sinnet.grpc.reports.report3.Models.CustomerDetails;
import sinnet.grpc.reports.report3.Models.GroupDetails;
import sinnet.grpc.reports.report3.Models.ReportRequest;

/** Converts ReportRequest (DTO) to its local model. */
interface DtoDomainMapper {

  static CustomerDetails toCustomerDetails(sinnet.report3.grpc.CustomerDetails x) { 
    return new CustomerDetails(x.getName(), x.getAddress(), x.getCity());
  }
  
  static GroupDetails toGroupDetails(sinnet.report3.grpc.GroupDetails x) {
    return new GroupDetails(x.getPersonName(), x.getDetailsList().stream().map(it -> toCustomerDetails(it)).toList());
  }
    
  static ReportRequest apply(sinnet.report3.grpc.ReportRequest x) {
    return new ReportRequest(x.getDetailsList().stream().map(it -> toGroupDetails(it)).toList());
  }
}
