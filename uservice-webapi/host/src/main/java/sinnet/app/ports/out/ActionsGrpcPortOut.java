package sinnet.app.ports.out;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import sinnet.domain.models.EntityId;
import sinnet.domain.models.TimeEntry;

/** GRPC methods related to Actions (aka Services aka Timeentries). */
public interface ActionsGrpcPortOut {

  /** Returns list of actions for requested project, limited result from-to range. */
  List<TimeEntry> searchInternal(UUID projectId, LocalDate from, LocalDate to);

  /** Fixme. */
  EntityId newAction(String requestorEmail, UUID projectId, LocalDate when);

  /** Fixme. */
  boolean update(EntityId entityId, String customerId, String description, int distance, int duration,
                 String servicemanEmail, String servicemanName, LocalDate whenProvided);

  /** Doxme. */
  boolean remove(UUID projectId, UUID entityId, int entityVersion);

  /** Fixme. */
  TimeEntry getActionInternal(UUID projectId, UUID entityId);
}
