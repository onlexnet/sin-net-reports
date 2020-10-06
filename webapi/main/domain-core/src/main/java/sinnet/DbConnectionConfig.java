package sinnet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;

@Configuration
public class DbConnectionConfig {

    @Value("${app.db.host}")
    private String dbHost;

    @Bean
    PgPool pgClient() {
        final var defaultPostgressPort = 5432;
        var connectOptions = new PgConnectOptions()
            .setPort(defaultPostgressPort)
            .setHost(dbHost)
            .setDatabase("sinnet")
            .setUser("sinnet")
            .setPassword("password");
        var poolOptions = new PoolOptions();

        return PgPool.pool(connectOptions, poolOptions);
    }
}
