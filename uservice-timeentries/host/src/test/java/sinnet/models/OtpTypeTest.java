package sinnet.models;

import java.util.function.Function;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class OtpTypeTest {
  
  @Test
  void shouldDeserialize() {
    Function<OtpType, OtpType> deser = it -> {
      var asDbo = OtpType.toDbo(it);
      return OtpType.fromDbo(asDbo);
    };

    var none = OtpType.None.INSTANCE;
    Assertions.assertThat(deser.apply(none)).isEqualTo(none);

    var totp = new OtpType.Totp("my secret", 42);
    Assertions.assertThat(deser.apply(totp)).isEqualTo(totp);
  }

}
