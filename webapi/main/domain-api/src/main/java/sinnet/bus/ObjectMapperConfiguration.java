package sinnet.bus;

import java.util.List;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.stereotype.Component;

import io.vavr.jackson.datatype.VavrModule;
import io.vertx.core.json.jackson.DatabindCodec;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

/** TBD. */
@Component
public class ObjectMapperConfiguration {
  
  @PostConstruct
  public void initVertx() {
    var jsonModules = jsonModules();
    for (var module : jsonModules.getItems()) {
      DatabindCodec.mapper().registerModule(module);
      DatabindCodec.prettyMapper().registerModule(module);
    }
  }

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
