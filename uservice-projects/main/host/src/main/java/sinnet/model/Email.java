package sinnet.model;

import lombok.Value;
import lombok.experimental.Accessors;

@Value(staticConstructor = "of")
@Accessors(fluent = true)
public class Email {
  private final String value;
}
