package sinnet.models;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ActionDurationTests {

    @Test
    public void shouldFormatString() {
        assertThat(ActionDuration.empty().toString()).isEqualTo("0:00");
        assertThat(ActionDuration.of(15).toString()).isEqualTo("0:15");
        assertThat(ActionDuration.of(7381).toString()).isEqualTo("123:01");
    }
}
