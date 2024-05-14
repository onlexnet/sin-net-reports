package sinnet.models;

import javax.annotation.Nullable;

/** One Type Password code generators. */ 
public sealed interface OtpType {

  /** Code generation is not supported. */ 
  enum None implements OtpType {
    INSTANCE
  }

  /** Code genrated using Sha1 with given counter. */
  record Totp(String secret, int counter) implements OtpType {
  }

  /** TBD. */
  static String toDbo(OtpType it) {
    return switch (it) {
      case None x -> null;
      case Totp(var secret, var counter) -> String.format("v1:%s|%s", secret, counter);
    };
  }

  /** TBD. */
  static OtpType fromDbo(@Nullable String dbo) {
    if (dbo == null) {
      return OtpType.None.INSTANCE;
    } else {
      var versionSeparator = dbo.indexOf(':') + 1;
      var values = dbo.substring(versionSeparator);
      var parts = values.split("\\|");
      var secret = parts[0];
      var counterAsString = parts[1];
      var counter = Integer.parseInt(counterAsString);
      return new OtpType.Totp(secret, counter);
    }
  }
}
