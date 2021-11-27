package sinnet.report2

import sinnet.reports.report2.{ReportRequest => ReportRequestDTO, ActivityDetails => ActivityDetailsDto}
import java.time.LocalDate
import scala.collection.JavaConverters._
import scala.util.Try
import sinnet.Minutes

/** Converts ReportRequest (DTO) to its local model. */
object DtoDomainMapper {
  import sinnet.Mapper._

  private implicit def toActivityDetails(x: ActivityDetailsDto) =
    ActivityDetails(x.getYearMonth(), x.getPersonName(), Minutes(x.getHowLongInMins()));
  // private implicit def toCustomerDetails(x: sinnet.reports.report1.CustomerDetails) =
  //   CustomerDetails(x.getCustomerName(), x.getCustomerCity(), x.getCustomerAddress())

    
  // implicit def apply(dto: Report1RequestDTO): ReportRequest = {
  //   ReportRequest(dto.getCustomer(), dto.getDetailsList().asScala.map(toActivityDetails _))
  // }
}
