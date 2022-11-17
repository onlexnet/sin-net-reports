package sinnet.models;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public final class CustomerSecret {
  private String location;
  private String username;
  private String password;
  private Email changedWho = Email.empty();
  private LocalDateTime changedWhen;
}
