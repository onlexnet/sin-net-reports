package sinnet.read;

import lombok.Builder;
import lombok.Value;
import sinnet.models.ValEmail;
import sinnet.models.EntityValue;

@Value
@Builder(toBuilder = true)
public class UserModel implements EntityValue<UserModel> {
  private ValEmail email;
}
