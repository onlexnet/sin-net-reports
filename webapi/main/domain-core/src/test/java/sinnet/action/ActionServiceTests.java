package sinnet.action;

import java.time.LocalDate;

import javax.sql.DataSource;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import sinnet.ActionService;
import sinnet.AppTestContext;
import sinnet.TestDatabase;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ContextConfiguration(classes = AppTestContext.class)
@TestPropertySource(value = {
    "/domain-core.properties"
})
@Testcontainers
public class ActionServiceTests {

    @Autowired
    private ActionService sut;

    @Container
    private static PostgreSQLContainer<?> database = new PostgreSQLContainer<>();

    @Test
    public void myTest() {
        Assertions.assertThat(sut).isNotNull();
        sut.find(LocalDate.now(), LocalDate.now());
    }

    @TestConfiguration
    public static class MyConfig {
        @Bean
        @Primary
        @ConfigurationProperties(prefix = "datasource")
        public DataSource dataSource() {
            var container = database;
            return DataSourceBuilder.create()
                .url(container.getJdbcUrl())
                .username(container.getUsername())
                .password(container.getPassword())
                .driverClassName(container.getDriverClassName())
                .build();
        }
    }
}
