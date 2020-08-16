package sinnet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/** Fixme. */
@Configuration
public class WebSecurityConfig {

    /**
     * Configures security for 'prod' profile'.
     * @return the configurer
     */
    @Bean
    @Profile("prod")
    public WebSecurityConfigurerAdapter webSecurityForProdProfile() {
        return new WebSecurityConfigurerAdapter() {
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
        };
    }

    /**
     * Configures security for 'dev' profile'.
     * @return the configurer
     */
    @Bean
    @Profile("dev")
    public WebSecurityConfigurerAdapter webSecurityForDevProfile() {
        return new WebSecurityConfigurerAdapter() {
            @Override
            protected void configure(final HttpSecurity http) throws Exception {
                http
                    .cors()
                    .and().csrf().disable()
                    .authorizeRequests().anyRequest().permitAll();
            }
        };
    }
}
