package sinnet.otp;

import java.util.stream.Stream;

/** Emits OTP codes. */
public interface OtpGenerator {

  /** TBD. */
  Stream<String> values(String secret, int period);

}
