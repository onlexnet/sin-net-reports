package sinnet.report3

import java.time.YearMonth
import sinnet.reports._

case class CustomerDetails(name: String, address: String, city: String)
case class GroupDetails(personName: String, details: Seq[CustomerDetails])

case class ReportRequest(
    activities: Seq[GroupDetails]
)
