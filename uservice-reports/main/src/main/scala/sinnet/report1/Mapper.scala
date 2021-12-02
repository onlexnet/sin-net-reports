package sinnet.report1

import sinnet.report1.grpc.{
  ReportRequest => Report1RequestDTO,
  ReportRequests => Report1RequestsDTO,
  ActivityDetails => ActivityDetailsDTO,
  CustomerDetails => CustomerDetailsDTO}
import java.time.LocalDate
import scala.collection.JavaConverters._
import scala.util.Try
import java.time.YearMonth
import sinnet.reports._

/** Converts ReportRequest (DTO) to its local model. */
object Mapper {
  private implicit def toActivityDetails(x: ActivityDetailsDTO) =
    ActivityDetails(x.getDescription(), x.getWho(), x.getWhen(), Minutes(x.getHowLongInMins()), Kilometers(x.getHowFarInKms()))
  private implicit def toCustomerDetails(x: CustomerDetailsDTO) =
    CustomerDetails(x.getCustomerName(), x.getCustomerCity(), x.getCustomerAddress())

  implicit def apply(dto: Report1RequestDTO): ReportRequest = {
    ReportRequest(dto.getCustomer(), dto.getDetailsList().asScala.map(toActivityDetails _))
  }
}
