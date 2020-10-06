package sinnet;

import lombok.Value;

@Value
public class UserModel implements EntityValue<UserModel> {
    private Email email;
}
