package sinnet;

import java.util.Map;

import org.testcontainers.containers.PostgreSQLContainer;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public final class InitDbTestResource implements QuarkusTestResourceLifecycleManager {

  private PostgreSQLContainer<?> container;
  private Runnable stop = () -> { };

  @Override
  public Map<String, String> start() {
    container = new PostgreSQLContainer<>("postgres:11-alpine");

    container.start();
    stop = container::stop;

    var username = container.getUsername();
    var password = container.getPassword();
    var jdbcUrl = container.getJdbcUrl();

    return Map.of(
      "DATABASE_USERNAME", username,
      "DATABASE_PASSWORD", password,
      "DATABASE_SCHEMA", "public",
      "DATABASE_JDBC", jdbcUrl);
  }

  @Override
  public void stop() {
    stop.run();
  }

}