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
    when: LocalDate,
    howLongInMins: TimeInMins,
    howFarInKms: DistanceInKms
)

class DistanceInKms(val value: Int) extends AnyVal {
    def +(m: DistanceInKms): DistanceInKms = new DistanceInKms(value + m.value)
}

class TimeInMins(val value: Int) extends AnyVal {
    def +(m: TimeInMins): TimeInMins = new TimeInMins(value + m.value)
}
