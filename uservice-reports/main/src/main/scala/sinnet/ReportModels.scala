package sinnet

import java.time.LocalDate

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

class Kilometers(val value: Int) extends AnyVal {
    def +(m: Kilometers): Kilometers = new Kilometers(value + m.value)
    override def toString: String = value.toString
}

object Kilometers {
    def apply(value: Int) = new Kilometers(value)
}

object Minutes {
    def apply(value: Int) = new Minutes(value)
}

class Minutes(val value: Int) extends AnyVal {
    def +(m: Minutes): Minutes = new Minutes(value + m.value)
    override def toString: String = {
        val minutesPerHour = 60
        var minutes = value % minutesPerHour
        var hours = value / minutesPerHour
        val minumuValueWithDoubleDigits = 10
        val minutesPrefix = if (minutes < minumuValueWithDoubleDigits) ":0" else ":"
        var minutesAsText = s"$minutesPrefix$minutes";
        hours + minutesAsText
    }
}
