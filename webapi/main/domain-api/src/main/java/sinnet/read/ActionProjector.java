package sinnet.read;

import java.time.LocalDate;
import java.util.UUID;

import io.vavr.collection.Array;
import io.vertx.core.Future;
import lombok.Builder;
import lombok.Value;
import sinnet.models.ActionValue;
import sinnet.models.Entity;
import sinnet.models.EntityId;

public interface ActionProjector {

  interface Provider {
    Future<Array<ListItem>> find(UUID projectId, LocalDate from, LocalDate to);
    Future<Entity<ActionValue>> find(UUID projectId, UUID entityId);
  }

  @Value
  @Builder
  class ListItem {
    private EntityId eid;
    private ActionValue value;
    private String servicemanName;
    private String customerName;
    private String customerCity;
    private String customerAddress;
  }
}
