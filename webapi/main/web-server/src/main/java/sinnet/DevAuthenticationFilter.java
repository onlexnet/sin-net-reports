package sinnet;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DevAuthenticationFilter extends AbstractAuthenticationProcessingFilter
                                     implements AuthenticationController {

    private String emailOfLoggedUser = "user1@project";

    protected DevAuthenticationFilter() {
        super("/graphql");
        super.setAuthenticationManager(new NoOpAuthenticationManager());
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        return new JwtAuthenticationToken(emailOfLoggedUser, emailOfLoggedUser);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authResult);
        chain.doFilter(request, response);
    }

    static class NoOpAuthenticationManager implements AuthenticationManager {
        @Override
        public Authentication authenticate(Authentication authentication) throws AuthenticationException {
            return null;
        }
    }

    @Override
    public void setEmail(String email) {
        // TODO Auto-generated method stub

    }
}
