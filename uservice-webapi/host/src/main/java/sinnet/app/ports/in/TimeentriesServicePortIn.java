package sinnet.app.ports.in;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import sinnet.domain.models.EntityId;
import sinnet.domain.models.TimeEntry;

public interface TimeentriesServicePortIn {

    List<TimeEntry> search(UUID projectId, LocalDate from, LocalDate to);

    TimeEntry getAction(UUID projectId, UUID entityId);
    
    boolean update(EntityId entityId, String customerId, String description, int distance, int duration,
                 String servicemanEmail, String servicemanName, LocalDate whenProvided);

    EntityId newEntry(String requestorEmail, UUID projectId, LocalDate entryDate);

    boolean remove(UUID projectId, UUID entityId, int entityVersion);
}
