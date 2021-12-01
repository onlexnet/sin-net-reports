/** Domain models related to Report2 */
package sinnet.report2

import java.time.YearMonth
import sinnet.Minutes
import sinnet.Kilometers

case class ActivityDetails(period: YearMonth, personName: String, kilometers: Kilometers, minutes: Minutes);

case class ReportRequest(
    activities: Seq[ActivityDetails]
)
