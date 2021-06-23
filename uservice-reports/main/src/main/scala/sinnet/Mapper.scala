package sinnet

import sinnet.reports.{ReportRequest => ReportRequestDTO, ReportRequests => ReportRequestsDTO, Response}
import java.time.LocalDate
import scala.collection.JavaConverters._
import scala.util.Try

object Mapper {
  private def def4(x: sinnet.reports.Date): Option[LocalDate] = 
    Try(LocalDate.of(x.getYear(), x.getMonth(), x.getDayOfTheMonth())).toOption
  private implicit def def3(x: sinnet.reports.ActivityDetails) =
    ActivityDetails(x.getDescription(), x.getWho(), def4(x.getWhen()), new Minutes(x.getHowLongInMins()), new Kilometers(x.getHowFarInKms()))
  private implicit def def2(x: sinnet.reports.CustomerDetails) =
    CustomerDetails(x.getCustomerName(), x.getCustomerCity(), x.getCustomerAddress())

    
  implicit def apply(dto: ReportRequestDTO): ReportRequest = {
    ReportRequest(dto.getCustomer(), dto.getDetailsList().asScala.map(def3 _))
  }
}
