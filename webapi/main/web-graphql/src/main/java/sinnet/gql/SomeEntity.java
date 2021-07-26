package sinnet.gql;

import java.util.UUID;

import lombok.Value;
import sinnet.models.EntityId;

@Value
public class SomeEntity implements Identified {
  private UUID projectId;
  private UUID entityId;
  private int entityVersion;

  public static SomeEntity of(EntityId eid) {
    return new SomeEntity(eid.getProjectId(), eid.getId(), eid.getVersion());
  }
}
