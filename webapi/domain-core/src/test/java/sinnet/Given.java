package sinnet;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

import lombok.val;

/** Utility class. */
public final class Given {

    /** FixMe.
     *
     * @return fixme
    */
    public static RegisterNewServiceAction createRegisterNewServiceAction() {
        val serviceActionId = UUID.randomUUID().toString();
        return new RegisterNewServiceAction(
            serviceActionId,
            Name.of("who"),
            LocalDate.now(),
            Name.of("whom"),
            "what",
            Duration.ofHours(1),
            Distance.of(2));
    }

}
