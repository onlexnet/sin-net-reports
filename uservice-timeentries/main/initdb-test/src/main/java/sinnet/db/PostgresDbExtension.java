package sinnet.db;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Reusable extension to start local PostgreSQL instance for µservices based on
 * its databases.
 */
public final class PostgresDbExtension implements BeforeAllCallback, AfterAllCallback {

  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14")
      .withUsername("testcontainers")
      .withPassword("testcontainers")
      .withDatabaseName("testcontainers");

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    postgres.start();
    System.setProperty("DATABASE_JDBC", postgres.getJdbcUrl());
    System.setProperty("DATABASE_USERNAME", postgres.getUsername());
    System.setProperty("DATABASE_PASSWORD", postgres.getPassword());
    System.setProperty("DATABASE_SCHEMA", "public");
  }

  @Override
  public void afterAll(ExtensionContext context) throws Exception {
    postgres.stop();
  }

}
