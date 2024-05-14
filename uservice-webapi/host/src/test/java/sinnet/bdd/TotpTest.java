package sinnet.bdd;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.bastiaanjansen.otp.HMACAlgorithm;
import com.bastiaanjansen.otp.TOTPGenerator;

import lombok.extern.slf4j.Slf4j;

public class TotpTest {

  static String exampleSecret = "JERAN5FEJNCN7C44YJCXAG6CI3MGPDNK63N3WPYKAVYUMHQ4FKP5VDOU6VLGU6PR";


  @Test
  void myTest1() throws InterruptedException {

    var when = LocalDateTime
      .of(2001,2,3,4,5,6)
      .toInstant(ZoneOffset.UTC);

    TOTPGenerator totp = new TOTPGenerator.Builder(exampleSecret.getBytes())
        .withHOTPGenerator(builder -> {
          builder.withPasswordLength(6);
          builder.withAlgorithm(HMACAlgorithm.SHA1); // SHA256 and SHA512 are also supported
        })
        .withPeriod(Duration.ofSeconds(30))
        .withClock(Clock.fixed(Instant.from(when), ZoneOffset.UTC))
        .build();
    
    Assertions.assertThat(totp.now()).isEqualTo("135172");
  }
}
