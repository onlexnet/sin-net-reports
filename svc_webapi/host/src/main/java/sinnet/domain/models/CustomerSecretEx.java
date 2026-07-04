package sinnet.domain.models;

import java.time.LocalDateTime;

public record CustomerSecretEx(
    String location,
    String username,
    String password,
    String entityName,
    String entityCode,
    String otpSecret,
    String otpRecoveryKeys,
    String changedWho,
    LocalDateTime changedWhen) { }
