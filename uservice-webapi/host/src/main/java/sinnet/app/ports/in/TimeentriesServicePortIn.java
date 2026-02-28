package sinnet.app.ports.in;

import java.time.LocalDate;
import java.util.UUID;

import sinnet.domain.models.EntityId;

public interface TimeentriesServicePortIn {
    
    EntityId newEntry(String requestorEmail, UUID projectId, LocalDate entryDate);
}
