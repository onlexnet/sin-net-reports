package sinnet;

import lombok.Value;

@Value
public class UserModel implements ValueObject {
    private String email;
}
