package sinnet;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/** Fixme. */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /** Fixme. */
    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
            .cors()
            .and()
                .csrf().disable()
            .authorizeRequests(conf -> {
                conf
                    .antMatchers("/graphql/**").authenticated()
                    .anyRequest().permitAll(); })
            .oauth2ResourceServer(conf -> {
                conf.jwt(jwt -> jwt.jwtAuthenticationConverter(new AuthenticationConverter())); });
    }
}
