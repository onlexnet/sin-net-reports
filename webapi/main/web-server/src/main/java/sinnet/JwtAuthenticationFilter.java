package sinnet;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nimbusds.jwt.SignedJWT;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

/**
 * Inspired by.
 * https://kelvinleong.github.io/authentication/2018/06/06/JWT-Authentication.html
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** fixme.
     * @param request fixme
     * @param response fixme
     * @param filterChain fixme
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {
        var bearerPrefix = "Bearer ";
        var bearerLength = bearerPrefix.length();
        var publicKeysUrl = "https://onlexnet.b2clogin.com/"
            + "f5230e02-babc-4b9d-ab7f-e76af49d1e5d/B2C_1_sign-in-or-up/discovery/v2.0/keys";
        Optional
            .ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
            .filter(StringUtils::isNotEmpty)
            .filter(it -> it.startsWith(bearerPrefix))
            .map(it -> it.substring(bearerLength, it.length()))
            .ifPresent(jwt -> {
                var a = Try.of(() -> SignedJWT.parse(jwt));
            });

        filterChain.doFilter(request, response);
    }
}
