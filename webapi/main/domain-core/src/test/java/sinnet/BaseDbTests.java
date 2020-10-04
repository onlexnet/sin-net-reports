package sinnet;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class BaseDbTests {

    protected BaseDbTests() {
    }

    static final PostgreSQLContainer<?> PG_CONTAINER;

    static {
        PG_CONTAINER = new PostgreSQLContainer<>();
        PG_CONTAINER.start();
    }

    @TestConfiguration
    public static class MyConfiguration {

        @Bean
        public static PropertySourcesPlaceholderConfigurer properties() {
            var pspc = new PropertySourcesPlaceholderConfigurer();
            var props = new Properties();
            var url = "r2dbc:postgresql://" +  PG_CONTAINER.getContainerIpAddress() + ":"
                    + PG_CONTAINER.getFirstMappedPort()
                    + "/" + PG_CONTAINER.getDatabaseName();
            props.setProperty("spring.r2dbc.url", url);
            props.setProperty("spring.r2dbc.username", PG_CONTAINER.getUsername());
            props.setProperty("spring.r2dbc.password", PG_CONTAINER.getPassword());
            pspc.setProperties(props);
            return pspc;
        }

        @Bean
        @ConfigurationProperties(prefix = "datasource")
        public DataSource dataSource() {
            return DataSourceBuilder.create()
                .url(PG_CONTAINER.getJdbcUrl())
                .username(PG_CONTAINER.getUsername())
                .password(PG_CONTAINER.getPassword())
                .driverClassName(PG_CONTAINER.getDriverClassName())
                .build();
        }
    }
}
