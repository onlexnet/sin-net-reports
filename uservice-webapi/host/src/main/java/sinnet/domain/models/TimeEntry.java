package sinnet.domain.models;

import java.time.LocalDate;

import org.jspecify.annotations.Nullable;

/** TBD. */
public record TimeEntry(
    EntityId entityId,
    @Nullable String customerId,
    String description,
    int distance,
    int duration,
    String servicemanEmail,
    String servicemanName,
    LocalDate whenProvided) {
    
}
