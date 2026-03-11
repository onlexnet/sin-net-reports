package sinnet.domain.models;

public record CustomerSecretEx(
    String location,
    String username,
    String password,
    String entityName,
    String entityCode,
    String otpSecret,
    String otpRecoveryKeys) { }
