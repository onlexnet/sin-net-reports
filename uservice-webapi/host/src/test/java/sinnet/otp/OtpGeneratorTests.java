package sinnet.otp;

import java.time.LocalDateTime;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.Getter;
import lombok.SneakyThrows;
import reactor.core.Disposable;
import reactor.core.Disposables;
import sinnet.infra.SecondsTicker;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = OtpConfigurer.class)
@Import(sinnet.otp.OtpGeneratorTests.TestConfig.class)
@Timeout(value = 3)
// not used by aplication logic
// but I like the code and Totp logic behind
public class OtpGeneratorTests {

  @Autowired
  OtpGenerator otpGenerator;

  @Autowired
  TestSecondsTicker secondsTicker;

  public static String exampleSecret = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
  public static LocalDateTime examplePeriod = LocalDateTime.of(2001, 2, 3, 4, 5, 6);
  public static String expectedCode = "175333";

  @Test
  @SneakyThrows
  void shouldGenerateValuesWithTime() {

    var actual = otpGenerator.values(exampleSecret, 30);
    secondsTicker.getTimeHandler().on(examplePeriod);
    var first = actual.findFirst();

    Assertions.assertThat(first).contains(expectedCode);
  }
  static class TestSecondsTicker implements SecondsTicker {

    @Getter
    private Handler timeHandler;
    
    @Override
    public Disposable schedule(Handler timeHandler) throws InterruptedException {
      this.timeHandler = timeHandler;
      return Disposables.single();
    }
  }

  public static class TestConfig {

    @Bean
    @Primary
    public TestSecondsTicker secondsTicker() {
      return new TestSecondsTicker();
    }

  }

}
