package sinnet.model;

import java.util.UUID;

import lombok.Value;
import lombok.experimental.Accessors;

@Value(staticConstructor = "of")
@Accessors(fluent = true)
public class ProjectIdHolder {
  private final UUID value;
}
