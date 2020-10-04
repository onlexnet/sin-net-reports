package sinnet;

import lombok.Value;

@Value
public class UserModel implements ValueObject<UserModel> {
    private Email email;
}
