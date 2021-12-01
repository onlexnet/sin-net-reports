package sinnet

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import sinnet.reports.Minutes

class MinutesTests {

    @Test
    def shouldFormatString: Unit = {
      Assertions.assertThat(Minutes(15).toString()).isEqualTo("0:15");
      Assertions.assertThat(Minutes(7381).toString()).isEqualTo("123:01");
    }
}