package sinnet.bdd;

import java.time.LocalDateTime;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import sinnet.app.lib.TimeProvider;

public class ServicesConfigurer {
    
  @Bean
  @Primary
  TimeProvider timeProvide(){
    return () -> LocalDateTime.of(2001, 2, 3, 4, 5, 6);
  }

}
