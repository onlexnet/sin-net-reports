package sinnet;

import com.microsoft.azure.spring.autoconfigure.b2c.AADB2COidcLoginConfigurer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import sinnet.jwt.JwtAuthenticationFilter;
import sinnet.jwt.KeyUtilHandler;

/** Fixme. */
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /** Fixme. */
    @Autowired
    private AADB2COidcLoginConfigurer aadAuthFilter;

    /** Fixme. */
    @Override
    protected void configure(final HttpSecurity http) throws Exception {

        var a = new KeyUtilHandler();
        var b = a.stringPublicKey();

        http
            .cors()
            .and()
                .csrf().disable()
            .authorizeRequests()
            .antMatchers("/graphql/**")
            .authenticated()
            .anyRequest().permitAll()
            .and()
                .headers().frameOptions().disable();

            var myFilter = new JwtAuthenticationFilter(b);
            http.addFilterBefore(myFilter,
                                 UsernamePasswordAuthenticationFilter.class);
    }
}
