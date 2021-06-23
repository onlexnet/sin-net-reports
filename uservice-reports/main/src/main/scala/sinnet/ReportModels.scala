package sinnet

import java.time.LocalDate

case class ReportRequest(
    customer: CustomerDetails,
    details: Seq[ActivityDetails]
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

class Kilometers(val value: Int) extends AnyVal {
    def +(m: Kilometers): Kilometers = new Kilometers(value + m.value)
}

object Kilometers {
    def apply(value: Int) = new Kilometers(value)
}

object Minutes {
    def apply(value: Int) = new Minutes(value)
}

class Minutes(val value: Int) extends AnyVal {
    def +(m: Minutes): Minutes = new Minutes(value + m.value)
}