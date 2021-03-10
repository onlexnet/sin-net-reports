package sinnet.gql;


import java.util.UUID;

import lombok.Data;

@Data
public class MyEntity {
  private UUID projectId;
  private UUID entityId;
  private int entityVersion;
}
