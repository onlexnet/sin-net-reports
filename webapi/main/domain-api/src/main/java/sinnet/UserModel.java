package sinnet;

import lombok.Builder;
import lombok.Value;
import sinnet.models.Email;
import sinnet.models.EntityValue;

@Value
@Builder(toBuilder = true)
public class UserModel implements EntityValue<UserModel> {
    private Email email;
}
