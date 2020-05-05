package sinnet;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

/** Register details about provided service. */
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class RegisterNewServiceAction {

    /** Unique ID of service action. */
    @With
    private String serviceActionId = UUID.randomUUID().toString();

    /** Serviceman who did the service. */
    private Name who;

    /** Date when the service has been provided. */
    private LocalDate when;

    /** Name of the Customer whom the service has been provided. */
    private Name whom;

    /** Descriptive info what actually has been provided as the service. */
    private String what;

    /** How much time take overall activity related to provide the service. */
    private Duration howLong;

    /** How far is distance to the place where service has been provided. */
    private Distance howFar;
}
