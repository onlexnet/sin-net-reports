package sinnet.db;

import java.util.List;

import org.testcontainers.containers.PostgreSQLContainer;

import lombok.extern.slf4j.Slf4j;

public final class PostgresDbRunner {

  private static final String TESTCONTAINERS = "testcontainers";
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14")
      .withUsername(TESTCONTAINERS)
      .withPassword(TESTCONTAINERS)
      .withDatabaseName(TESTCONTAINERS);

  public SafeAutoCloseable start() {
    postgres.start();
    var items = List.of(
        setProperty("DATABASE_JDBC", postgres.getJdbcUrl()),
        setProperty("DATABASE_USERNAME", postgres.getUsername()),
        setProperty("DATABASE_PASSWORD", postgres.getPassword()),
        setProperty("DATABASE_SCHEMA", "public"));
    return () -> items.forEach(SafeAutoCloseable::close);
  }

  private SafeAutoCloseable setProperty(String propertyName, String propertyValue) {
    var actual = System.getProperty(propertyName);
    System.setProperty(propertyName, propertyValue);
    return actual == null
        ? () -> System.clearProperty(propertyName)
        : () -> System.setProperty(propertyName, actual);
  }

  @FunctionalInterface
  public interface SafeAutoCloseable extends AutoCloseable {
    void close();
  }

}