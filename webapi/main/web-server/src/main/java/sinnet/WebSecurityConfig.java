package sinnet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
// import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Fixme.
*/
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    /** Fixme. */
    @Autowired
    private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService;

    /**
     * Fixme.
     * @param http fixme
     */
    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
            .authorizeRequests()

            // .antMatchers("/graphql").permitAll()
            // .antMatchers("/vendor/**").permitAll()
            // .antMatchers("/graphiql").permitAll()

            .anyRequest().authenticated()
            .and()
            .oauth2Login()
            .userInfoEndpoint()
            .oidcUserService(oidcUserService);

            // .and()
            // .logout()
            //  .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            // .logoutSuccessUrl("/")
            //    .deleteCookies("JSESSIONID").invalidateHttpSession(true)
    }
}
