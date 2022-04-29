package sinnet.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@AllArgsConstructor
@Value
@Jacksonized
@Builder(toBuilder = true)
public final class CustomerContact {
  private String firstName;
  private String lastName;
  private String phoneNo;
  private String email;
}
