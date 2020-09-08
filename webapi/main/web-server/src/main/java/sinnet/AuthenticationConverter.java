package sinnet;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

/** Fixme. */
public class AuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    /** {@inheritDoc} */
    @Override
    public AbstractAuthenticationToken convert(final Jwt source) {
        var accountId = source.getClaimAsString("oid");
        var email = source.getClaimAsStringList("emails").get(0);
        return new JwtAuthenticationToken(accountId, email);
    }

}

/** Fixme. */
final class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private String email;
    /**
     * Fixme.
     * @param accountId fixme
     * @param email fixme
     */
    JwtAuthenticationToken(final String accountId,
                           final String email) {
        super(null);
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

