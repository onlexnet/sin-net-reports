package sinnet.models;

import java.util.UUID;
import lombok.Value;

/**
 * TBD.
 */
@Value
public class ProjectId {

  private UUID id;
  private long version;

  public static ProjectId of(UUID id, long version) {
    return new ProjectId(id, version);
  }

  public static ProjectId anyNew() {
    return new ProjectId(UUID.randomUUID(), 0);
  }
}
