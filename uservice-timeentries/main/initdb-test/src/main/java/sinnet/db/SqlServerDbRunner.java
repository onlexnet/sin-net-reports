package sinnet.db;

import java.util.List;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MSSQLServerContainer;


/** Will be described. */
public final class SqlServerDbRunner {

  private static final String TESTCONTAINERS = "testcontainers";
  private static final String DBPASSWORD = "A_Str0ng_Required_Password";
  static JdbcDatabaseContainer database = new MSSQLServerContainer()
      .acceptLicense();

  /** Will be described. */
  public SafeAutoCloseable start() {
    database.start();
    var items = List.of(
        setProperty("DATABASE_HOST", database.getHost()),
        // setProperty("DATABASE_NAME", database.getDatabaseName()),
        setProperty("DATABASE_NAME", "master"),
        setProperty("DATABASE_PORT", String.valueOf(database.getFirstMappedPort())),
        setProperty("DATABASE_USERNAME", database.getUsername()),
        setProperty("DATABASE_PASSWORD", database.getPassword()),
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

  /** Will be described. */
  @FunctionalInterface
  public interface SafeAutoCloseable extends AutoCloseable {
    void close();
  }

}
