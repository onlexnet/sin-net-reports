package sinnet.security;

import javax.enterprise.context.ApplicationScoped;

import io.vavr.control.Option;

@ApplicationScoped
public class IdentityProviderImpl implements IdentityProvider {

    @Override
    public Option<User> getCurrent() {
        // var context = SecurityContextHolder.getContext();
        // var auth = (AppAuthenticationToken) context.getAuthentication();
        // if (auth == null) return Optional.empty();
        // var result = new User(auth.getEmail());
        // return Optional.of(result);
        return Option.none();
    }
}
