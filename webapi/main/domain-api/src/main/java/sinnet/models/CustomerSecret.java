package sinnet.models;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@AllArgsConstructor
@Value
@Jacksonized
@Builder(toBuilder = true)
public final class CustomerSecret {
  private String location;
  private String username;
  private String password;
  @Builder.Default
  private Email changedWho = Email.empty();
  private LocalDateTime changedWhen;
}
