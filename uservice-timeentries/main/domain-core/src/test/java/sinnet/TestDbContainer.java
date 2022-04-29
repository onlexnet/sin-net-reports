package sinnet;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.PostgreSQLContainer;

import io.vertx.core.Vertx;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import lombok.Getter;

@TestConfiguration
public class TestDbContainer {

    @Getter
    private PostgreSQLContainer<?> container;

    @PostConstruct
    public void init() {
        container = new PostgreSQLContainer<>();
        container.start();
    }

    @PreDestroy
    public void dispose() {
        container.stop();
    }

    @Bean
    @ConfigurationProperties(prefix = "datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create()
            .url(container.getJdbcUrl())
            .username(container.getUsername())
            .password(container.getPassword())
            .driverClassName(container.getDriverClassName())
            .build();
    }

    @Bean
    @Primary
    PgPool pgTestClient() {
        var connectOptions = new PgConnectOptions()
        .setPort(container.getFirstMappedPort())
        .setHost(container.getContainerIpAddress())
        .setDatabase(container.getDatabaseName())
        .setUser(container.getUsername())
        .setPassword(container.getPassword());

        var poolOptions = new PoolOptions();

        return PgPool.pool(connectOptions, poolOptions);
    }

  @Bean
  @Primary
  JDBCPool jdbcPool(Vertx vertx) {
    var connOptions = new JDBCConnectOptions()
        .setJdbcUrl(container.getJdbcUrl())
        .setUser(container.getUsername())
        .setPassword(container.getPassword());
    var someMaxPoolSize = 16;
    var poolOptions = new PoolOptions()
        .setMaxSize(someMaxPoolSize);
    return JDBCPool.pool(vertx, connOptions, poolOptions);
  }

}
