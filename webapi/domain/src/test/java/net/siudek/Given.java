package net.siudek;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

import lombok.val;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Given {

    public static RegisterNewServiceCommand registerNewServiceCommand() {
        val serviceActionId = UUID.randomUUID().toString();
        return new RegisterNewServiceCommand(
            serviceActionId,
            Name.of("who"),
            LocalDate.now(),
            Name.of("whom"),
            "what",
            Duration.ofHours(1),
            new Distance(0));
    }

}