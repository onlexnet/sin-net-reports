package sinnet.read;

import lombok.Builder;
import lombok.Value;
import sinnet.domain.model.ValEmail;
import sinnet.models.EntityValue;
import sinnet.models.ValName;

/**
 * TBD.
 */
@Value
@Builder(toBuilder = true)
public class UserModel implements EntityValue<UserModel> {
  private ValEmail email;
  private ValName customName;
}
