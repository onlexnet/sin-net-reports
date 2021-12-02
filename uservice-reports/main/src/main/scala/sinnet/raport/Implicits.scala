package sinnet.reports

import java.time.LocalDate
import scala.collection.JavaConverters._
import scala.util.Try
import sinnet.reports.grpc.{Date => DateDTO, YearMonth => YearMonthDTO}

/** Converts ReportRequest (DTO) to its local model. */
trait Implicits {
  implicit def toLocalDate(x: DateDTO): Option[LocalDate] = Try(LocalDate.of(x.getYear(), x.getMonth(), x.getDayOfTheMonth())).toOption
  implicit def toLocal(x: YearMonthDTO) = java.time.YearMonth.of(x.getYear(), x.getMonth());
}
