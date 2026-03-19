package sinnet.domain.models;

import java.time.LocalDateTime;

public record CustomerSecret(
    String location,
    String username,
    String password,
    String otpSecret,
    String otpRecoveryKeys,
    String changedWho,
    LocalDateTime changedWhen) { }
