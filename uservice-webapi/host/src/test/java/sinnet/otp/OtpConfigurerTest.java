package sinnet.otp;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class OtpConfigurerTest {

  @Test
  void testOtpConfigurerInstantiation() {
    // Act
    var configurer = new OtpConfigurer();

    // Assert
    assertNotNull(configurer);
  }
}