package sinnet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.vertx.core.tracing.TracingPolicy;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;

@Configuration
@Profile({ "prod", "dev" })
public class DbConnectionConfig {

  @Value("${app.db.host}")
  private String dbHost;

  @Value("${app.db.name}")
  private String dbName;

  @Bean
  PgPool pgClient() {
    final var defaultPostgressPort = 5432;
    var connectOptions = new PgConnectOptions()
        .setPort(defaultPostgressPort)
        .setHost(dbHost)
        .setDatabase(dbName)
        .setUser("sinnet")
        .setTracingPolicy(TracingPolicy.ALWAYS)
        .setPassword("sinnet");
    var poolOptions = new PoolOptions();

    return PgPool.pool(connectOptions, poolOptions);
  }
}
