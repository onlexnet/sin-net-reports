package sinnet.report3

import sinnet.report3.grpc.{
  ReportRequest => ReportRequestDTO,
  CustomerDetails => CustomerDetailsDto,
  GroupDetails => GroupDetailsDto}
import java.time.LocalDate
import scala.collection.JavaConverters._
import scala.util.Try
import sinnet.reports._

/** Converts ReportRequest (DTO) to its local model. */
object DtoDomainMapper {

  implicit def toCustomerDetails(x: CustomerDetailsDto) = CustomerDetails(x.getName(), x.getAddress(), x.getCity());
  implicit def toGroupDetails(x: GroupDetailsDto) = GroupDetails(x.getPersonName(), x .getDetailsList.asScala.map(toCustomerDetails _));
    
  implicit def apply(dto: ReportRequestDTO): ReportRequest = {
    ReportRequest(dto.getDetailsList.asScala.map(toGroupDetails _))
  }
}
