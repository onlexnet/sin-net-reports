package sinnet;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.boot.SpringApplication;

public final class Program {

  public static void main(final String[] args) {
    // https://vertx.io/docs/vertx-sql-client-templates/java/#_java_datetime_api_mapping
    var mapper = io.vertx.core.json.jackson.DatabindCodec.mapper();
    mapper.registerModule(new JavaTimeModule());

    SpringApplication.run(Application.class, args);
  }
}
