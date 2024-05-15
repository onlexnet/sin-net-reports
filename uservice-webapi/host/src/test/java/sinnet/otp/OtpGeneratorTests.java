package sinnet.otp;

import java.time.LocalDateTime;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
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
@Timeout(value = 3)
public class OtpGeneratorTests {

  @Autowired
  OtpGenerator otpGenerator;

  @Autowired
  TestSecondsTicker secondsTicker;

  @Test
  @SneakyThrows
  void shouldGenerateValuesWithTime() {

    var exampleSecret = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    var exampleTime = LocalDateTime.of(2001, 2, 3, 4, 5, 6);
    var expectedCode = "175333";

    var actual = otpGenerator.values(exampleSecret, 30);
    secondsTicker.getTimeHandler().on(exampleTime);
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

  @TestConfiguration
  public static class TestConfig {

    @Bean
    @Primary
    public TestSecondsTicker secondsTicker() {
      return new TestSecondsTicker();
    }

  }
}