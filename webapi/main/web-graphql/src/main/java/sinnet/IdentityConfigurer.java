package sinnet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class IdentityConfigurer {

    @Bean
    public Identity current() {
        var context = SecurityContextHolder.getContext();
        var auth = context.getAuthentication();
        return new Identity() {
            @Override
            public String getEmail() {
                return "siudeks@gmail.com";
            }
        };
    }
}
