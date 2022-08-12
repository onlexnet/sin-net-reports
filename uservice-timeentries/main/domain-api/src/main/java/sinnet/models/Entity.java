package sinnet.models;

import java.util.UUID;

import lombok.Value;

@Value
public class Entity<VALUE> {
  private UUID projectId;
  private UUID entityId;
  private long version;
  private VALUE value;
}
