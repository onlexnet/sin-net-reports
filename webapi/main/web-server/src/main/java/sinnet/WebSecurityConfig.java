package sinnet;

import com.microsoft.azure.spring.autoconfigure.b2c.AADB2COidcLoginConfigurer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/** Fixme. */
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /** Fixme. */
    @Autowired
    private AADB2COidcLoginConfigurer configurer;

    /** Fixme. */
    @Override
    protected void configure(final HttpSecurity http) throws Exception {
            // http
            //     .authorizeRequests()
            //     .anyRequest()
            //     .authenticated()
            //     .and()
            //     .apply(configurer);
            http
                .cors()
                .and()
                .csrf()
                .disable()
                .authorizeRequests()
                .anyRequest()
                .permitAll()
                .and()
                .apply(configurer);

    }
}
