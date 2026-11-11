package sinnet.domain.models;

import java.time.LocalDateTime;

public record CustomerSecret(
    String location,
    String username,
    String password,
    String otpSecret,
    String otpRecoveryKeys,
    String changedWho,
    LocalDateTime changedWhen,
    AuthorizationGroup authorizationGroup) {

  public CustomerSecret {
    java.util.Objects.requireNonNull(authorizationGroup, "authorizationGroup");
  }

  public CustomerSecret(String location, String username, String password, String otpSecret,
                        String otpRecoveryKeys, String changedWho, LocalDateTime changedWhen) {
    this(location, username, password, otpSecret, otpRecoveryKeys, changedWho, changedWhen,
        AuthorizationGroup.SINNET);
  }
}
