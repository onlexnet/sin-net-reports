package sinnet.domain.models;

public record CustomerSecret(
    String location,
    String username,
    String password,
    String otpSecret,
    String otpRecoveryKeys) { }
