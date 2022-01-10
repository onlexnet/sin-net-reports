package sinnet.report2

import sinnet.report2.grpc.{
  ReportRequest => ReportRequestDTO,
  ActivityDetails => ActivityDetailsDto}
import java.time.LocalDate
import scala.collection.JavaConverters._
import scala.util.Try
import sinnet.reports._

/** Converts ReportRequest (DTO) to its local model. */
object DtoDomainMapper {

  implicit def toActivityDetails(x: ActivityDetailsDto) = ActivityDetails(x.getYearMonth(), x.getPersonName(), Kilometers(x.getHowFarInKms()), Minutes(x.getHowLongInMins()));
    
  implicit def apply(dto: ReportRequestDTO): ReportRequest = {
    ReportRequest(dto.getDetailsList.asScala.map(toActivityDetails _))
  }
}
