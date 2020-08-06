package sinnet;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

/** Fixme. */
public class AuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    /** {@inheritDoc} */
    @Override
    public AbstractAuthenticationToken convert(final Jwt source) {
        var authorities = Optional
            .ofNullable(source.getClaimAsStringList("roles"))
            .stream()
            .flatMap(it -> it.stream())
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
        return new UsernamePasswordAuthenticationToken("", "", authorities);
    }

}

/** Fixme. */
final class JwtAuthenticationToken extends AbstractAuthenticationToken {

    /**
     * Fixme.
     * @param authorities fixme
     */
    JwtAuthenticationToken(final Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        // TODO Auto-generated constructor stub
    }

    private static final long serialVersionUID = 3717651672219649325L;

    @Override
    public Object getCredentials() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getPrincipal() {
        // TODO Auto-generated method stub
        return null;
    }
}

