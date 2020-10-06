package sinnet;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class UserModel implements EntityValue<UserModel> {
    private Email email;
}
