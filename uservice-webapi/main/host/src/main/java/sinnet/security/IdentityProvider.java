package sinnet.security;

import io.vavr.control.Option;
import lombok.Value;

public interface IdentityProvider {

    Option<User> getCurrent();
  
    @Value
    class User {
      private String email;
    }
  }
  