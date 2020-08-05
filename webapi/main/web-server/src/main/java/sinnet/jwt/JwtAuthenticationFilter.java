package sinnet.jwt;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

/**
 * Inspired by.
 * https://kelvinleong.github.io/authentication/2018/06/06/JWT-Authentication.html
 * https://www.scottbrady91.com/Kotlin/Creating-Signed-JWTs-using-Nimbus-JOSE-JWT
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // There is more types
    // http://mitreid-connect.github.io/apidocs/src-html/org/
    // mitre/jwt/signer/service/impl/DefaultJWTSigningAndValidationService.html
    private final RSAKey key;
    /**
     * fixme.
     * @param key fixme
     */
    public JwtAuthenticationFilter(final RSAKey key) {
        this.key = key;
    }

    /** fixme.
     * @param request fixme
     * @param response fixme
     * @param filterChain fixme
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {
                                        // ECKey.parse(jwk.toJSONString()).toECPublicKey();
        // var a = new ECDSAVerifier(key.toECPublicKey());
        var bearerPrefix = "Bearer ";
        var bearerLength = bearerPrefix.length();
        Optional
            .ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
            .filter(StringUtils::isNotEmpty)
            .filter(it -> it.startsWith(bearerPrefix))
            .map(it -> it.substring(bearerLength, it.length()))
            .ifPresent(jwt -> {
                var a = Try
                    .of(() -> {
                        var isValid  = SignedJWT
                            .parse(jwt)
                            .verify(new RSASSAVerifier(key));
                        return isValid;
                    });
            });

        filterChain.doFilter(request, response);
    }
}
