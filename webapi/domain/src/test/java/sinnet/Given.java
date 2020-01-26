package sinnet;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

import lombok.val;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Given {

    /** FixMe.
     *
     * @return fixme
    */
    public static RegisterNewServiceActionCommand registerNewServiceActionCommand() {
        val serviceActionId = UUID.randomUUID().toString();
        return new RegisterNewServiceActionCommand(
            serviceActionId,
            Name.of("who"),
            LocalDate.now(),
            Name.of("whom"),
            "what",
            Duration.ofHours(1),
            new Distance(0));
    }

}
