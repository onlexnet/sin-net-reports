package sinnet.reports

import java.time.LocalDate
import scala.collection.JavaConverters._
import scala.util.Try
import sinnet.reports.grpc.{Date => DateDTO, YearMonth => YearMonthDTO}
import java.time.YearMonth

/** Converts ReportRequest (DTO) to its local models. */
trait Implicits {
  implicit def toLocalDate(x: DateDTO): Option[LocalDate] = Try(LocalDate.of(x.getYear(), x.getMonth(), x.getDayOfTheMonth())).toOption
  implicit def toYearMonth(x: YearMonthDTO) = YearMonth.of(x.getYear(), x.getMonth());
}
