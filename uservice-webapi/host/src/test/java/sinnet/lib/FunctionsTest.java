package sinnet.lib;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class FunctionsTest {
    
    @Test
    void shouldCreateFunction() {

        var function = Functions.of((Integer it) -> it * 2);

        Assertions.assertThat(function.apply(2)).isEqualTo(4);
    }
}
