package sinnet.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

/** Fixme. */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

  /** Fixme. */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .cors(cors -> cors
            .configurationSource(request -> new CorsConfiguration()
            
            .applyPermitDefaultValues()))
        .csrf(it -> it.disable())
        .httpBasic(it -> it.disable())
        .addFilterBefore(new CustomAuthenticationFilter(), BasicAuthenticationFilter.class)
        .build();
  }

}
