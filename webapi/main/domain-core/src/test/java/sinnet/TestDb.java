package sinnet;

import org.testcontainers.containers.PostgreSQLContainer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestDb extends PostgreSQLContainer {

    private static final String IMAGE_VERSION = "postgres:alpine";
    private static TestDb container;

    private TestDb() {
      super(IMAGE_VERSION);
    }

    public static TestDb instance() {
      if (container == null) {
        container = new TestDb();
      }
      return container;
    }

    @Override
    public void start() {
      super.start();
    //   logger.debug("POSTGRES INFO");
    //   logger.debug("DB_URL: " + container.getJdbcUrl());
    //   logger.debug("DB_USERNAME: " + container.getUsername());
    //   logger.debug("DB_PASSWORD: " + container.getPassword());
      System.setProperty("DB_URL", container.getJdbcUrl());
      System.setProperty("DB_USERNAME", container.getUsername());
      System.setProperty("DB_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
      //do nothing, JVM handles shut down
    }
  }

