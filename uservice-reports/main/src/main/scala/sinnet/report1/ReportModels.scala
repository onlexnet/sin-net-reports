package sinnet.report1

import java.time.LocalDate
import sinnet.reports._

/**
  * Represents printable data content of a report. Useful for testing before being converted to PDF file.
  */
case class ReportRequest(
    /** Details about the customer whome the activities have been addressed. */
    customer: CustomerDetails,
    /** List of the activities. */
    activities: Seq[ActivityDetails]
)

case class CustomerDetails(
    customerName: String,
    customerCity: String,
    address: String
)

case class ReportRequests(items: Seq[ReportRequest])

case class ActivityDetails(
    description: String,
    who: String,
    when: Option[LocalDate],
    howLongInMins: Minutes,
    howFarInKms: Kilometers
)

case class SpecialActivityDetails(
    description: String,
    who: String,
    when: Option[LocalDate],
    howLongInMins: Minutes,
    howFarInKms: Kilometers
)
