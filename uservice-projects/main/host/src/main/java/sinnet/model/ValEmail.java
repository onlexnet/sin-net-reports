package sinnet.model;

import lombok.Value;
import lombok.experimental.Accessors;

@Value(staticConstructor = "of")
@Accessors(fluent = true)
public class ValEmail {
  private final String value;
}
