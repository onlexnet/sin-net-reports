package sinnet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.vertx.core.Vertx;
import io.vertx.core.tracing.TracingPolicy;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import lombok.Setter;

@Configuration
@Profile({ "prod", "dev" })
public class DbConnectionConfigurer {

  private String datasourceUrl;
  private String dbHost;
  private String dbName;
  private String username;
  private String password;

  @Value("${spring.datasource.url}")
  void setDatasourceUrl(String value) {
    datasourceUrl = value;
  }

  @Value("${app.db.host}")
  void setDbHost(String value) {
    dbHost = value;
  }

  @Value("${app.db.name}")
  void setDbName(String value) {
    dbName = value;
  }

  @Value("${spring.datasource.username}")
  void setUsername(String value) {
    username = value;
  }

  @Value("${spring.datasource.password}")
  void setPassword(String value) {
    password = value;
  }


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

  @Bean
  JDBCPool jdbcPool(Vertx vertx) {
    var connOptions = new JDBCConnectOptions()
        .setJdbcUrl(datasourceUrl)
        .setUser(username)
        .setPassword(password);
    var someMaxPoolSize = 16;
    var poolOptions = new PoolOptions()
        .setMaxSize(someMaxPoolSize);
    return JDBCPool.pool(vertx, connOptions, poolOptions);
  }
}
