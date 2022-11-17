package sinnet;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.integration.spring.SpringLiquibase;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.Cleanup;
import lombok.SneakyThrows;

@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Testcontainers
public class ProgramTest {

  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:11")
      .withUsername("testcontainers")
      .withPassword("testcontainers")
      .withDatabaseName("testdb");

  @DynamicPropertySource
  static void registerPgProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", () -> postgres.getJdbcUrl());
    registry.add("spring.datasource.username", () -> postgres.getUsername());
    registry.add("spring.datasource.password", () -> postgres.getPassword());
  }

  @Autowired
  SpringLiquibase liquibaseBean;

  // We need rollback for database operations so that rollback procedure is
  // required for all liquibase changesets.

  @Test
  @SneakyThrows
  void should_rollback_all_changes() {
    var dataSource = liquibaseBean.getDataSource();
    var connection = dataSource.getConnection();
    var database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
    @Cleanup
    var liquibase = new Liquibase("db/changelog-master.yaml", new ClassLoaderResourceAccessor(), database);
    var initialTag = "v0"; // well-known tag in database used for rollback tests
    Assertions
        .assertThatCode(() -> liquibase.rollback(initialTag, (String) null))
        .as("All operation should include rollback operation")
        .doesNotThrowAnyException();
  }

}
