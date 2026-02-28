package sinnet.domain.models;

import java.time.LocalDate;

/** TBD. */
public record TimeEntry(
    EntityId entityId,
    String customerId,
    String description,
    int distance,
    int duration,
    String servicemanEmail,
    String servicemanName,
    LocalDate whenProvided) {
    
}
