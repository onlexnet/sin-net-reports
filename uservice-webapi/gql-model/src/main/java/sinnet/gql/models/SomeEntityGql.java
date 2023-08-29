package sinnet.gql.models;

import lombok.Data;
import lombok.experimental.Accessors;

/** Fixme. */
@Data
@Accessors(chain = true)
public class SomeEntityGql {
  private String projectId;
  private String entityId;
  private Long entityVersion;
}
