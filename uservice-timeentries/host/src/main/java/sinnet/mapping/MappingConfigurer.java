package sinnet.mapping;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tools.jackson.databind.ObjectMapper;

@Configuration
class MappingConfigurer {

  @Bean
  ObjectMapper objectMapper() {
    return new ObjectMapper();
  }
  
}
