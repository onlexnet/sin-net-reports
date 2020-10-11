package sinnet;

import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class IdentityProviderImpl implements IdentityProvider {

    @Override
    public Optional<User> getCurrent() {
        var context = SecurityContextHolder.getContext();
        var auth = (AppAuthenticationToken) context.getAuthentication();
        var result = new User("siudeks@gmail.com");
        return Optional.of(result);
    }
}
