package sinnet.reports

class Kilometers(val value: Int) extends AnyVal {
    def +(m: Kilometers): Kilometers = new Kilometers(value + m.value)
    override def toString: String = value.toString
}

object Kilometers {
    def apply(value: Int) = new Kilometers(value)
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

object Minutes {
    def apply(value: Int) = new Minutes(value)
}

