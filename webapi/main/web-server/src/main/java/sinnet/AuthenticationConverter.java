package sinnet;

import java.util.Optional;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

import io.vavr.control.Try;

/** Fixme. */
public class AuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    /** {@inheritDoc} */
    @Override
    public AbstractAuthenticationToken convert(final Jwt source) {
        // both claims below are not availabe by default in B2C.
        // you have to configure user flows -> your sign in policy -> application claims
        // and select 'User's Object ID' and 'Email Addresses' and 'User is new'
        var accountId = source.getClaimAsString("oid");
        // claim 'newUser' is optional available only when iser is logging first time to the app
        var newUser = Optional
            .ofNullable(source.containsClaim("newUser"))
            .map(ignored -> source.getClaimAsBoolean("newUser"))
            .orElse(false);
        var name = source.getClaimAsString("name");
        return Try
            .of(() -> source.getClaimAsStringList("emails").get(0))
            .map(email -> new JwtAuthenticationToken(accountId, email, name, newUser))
            .getOrElseThrow(() -> new IllegalArgumentException("JWT shoulw contain 'emails' claim, required by the application."));
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
    JwtAuthenticationToken(String accountId,
                           String email,
                           String name,
                           boolean newUser) {
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

