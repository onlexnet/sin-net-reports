package sinnet;

import java.time.Duration;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Value;

/** Register details about provided service. */
@Value
@Builder(toBuilder = true)
public class ServiceEntity implements EntityValue<ServiceEntity> {

    /** Serviceman who did the service. */
    @Builder.Default
    private Name who = Name.empty();

    /** Date when the service has been provided. */
    private LocalDate when;

    /** Name of the Customer whom the service has been provided. */
    @Builder.Default
    private Name whom = Name.empty();

    /** Descriptive info what actually has been provided as the service. */
    private String what;

    /** How much time take overall activity related to provide the service. */
    private Duration howLong;

    /** How far is distance to the place where service has been provided. */
    @Builder.Default
    private Distance howFar = Distance.empty();
}
