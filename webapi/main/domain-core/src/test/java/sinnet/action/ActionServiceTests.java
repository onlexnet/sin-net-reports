package sinnet.action;

import java.time.LocalDate;

import javax.activation.DataSource;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import sinnet.ActionService;
import sinnet.AppTestContext;
import sinnet.TestDb;

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
    private PostgreSQLContainer<?> postgreSQLContainer = TestDb.instance();

    @Test
    public void myTest() {
        Assertions.assertThat(sut).isNotNull();
        sut.find(LocalDate.now(), LocalDate.now());
    }

    @TestConfiguration
    public static class MyConfig {
        @Bean
        public DataSource dataSource() {
            var container = postgreSQLContainer;
            var dsb = DataSourceBuilder.create();
            dsb.url(container.getJdbcUrl());
            dsb.username(container.getUsername());
            dsb.password(container.getPassword());
            dsb.driverClassName(container.getDriverClassName());
            return dsb.build();
        }
    }
}
