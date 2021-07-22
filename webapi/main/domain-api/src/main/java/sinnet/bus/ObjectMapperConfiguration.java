package sinnet.bus;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.vavr.jackson.datatype.VavrModule;
import io.vertx.core.json.jackson.DatabindCodec;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

/** TBD. */
@Configuration
public class ObjectMapperConfiguration {
  
  /** Extends vert.x serializer with reusable configured serializer. */
  @Autowired
  public void initVertx(AdditionalModules jsonModules) {
    for (var module : jsonModules.getItems()) {
      DatabindCodec.mapper().registerModule(module);
      DatabindCodec.prettyMapper().registerModule(module);
    }
  }

  // /** Reusable standalone jackson serializer used e.g. in test. It is also registerd in VertX by the method above. */
  // @Bean
  // public ObjectMapper objectMapper(AdditionalModules jsonModules) {
  //   var objectMapper = new ObjectMapper();
  //   for (var module : jsonModules.getItems()) {
  //     objectMapper.registerModule(module);
  //   }
  //   return objectMapper;
  // }

  @Bean
  public AdditionalModules jsonModules() {
    return AdditionalModules.builder()
      .item(new JavaTimeModule()) // https://vertx.io/docs/vertx-sql-client-templates/java/#_java_datetime_api_mapping
      .item(new VavrModule())
      .build();
  }
}

@Value
@Builder
class AdditionalModules {
  @Singular
  private List<SimpleModule> items;
}
