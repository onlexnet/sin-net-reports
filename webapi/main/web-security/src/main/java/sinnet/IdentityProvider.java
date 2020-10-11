package sinnet;

import java.util.Optional;

import lombok.Value;

public interface IdentityProvider {

    Optional<User> getCurrent();

    @Value
    class User {
        private String email;
    }
}
