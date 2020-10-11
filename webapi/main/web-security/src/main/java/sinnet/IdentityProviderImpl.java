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
        if (auth == null) return Optional.empty();
        var result = new User(auth.getEmail());
        return Optional.of(result);
    }
}
