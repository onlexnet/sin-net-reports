package sinnet;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;

/** Fixme. */
final class AppAuthenticationToken extends AbstractAuthenticationToken {

    @Getter
    private String email;

    /**
     * Fixme.
     * @param accountId fixme
     * @param email fixme
     */
    AppAuthenticationToken(String accountId,
                           String email,
                           String name,
                           boolean newUser,
                           Collection<GrantedAuthority> authorities) {
        super(authorities);
        this.email = email;
        this.setAuthenticated(true);
    }

    private static final long serialVersionUID = 3717651672219649325L;

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return email;
    }
}
