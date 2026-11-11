package sinnet.grpc.customers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.SneakyThrows;

@Testcontainers
class AuthorizationGroupsMigrationTest {

  @Container
  static final MSSQLServerContainer<?> DATABASE =
      new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-CU17-ubuntu-22.04").acceptLicense();

  @Test
  @SneakyThrows
  void shouldMigrateFreshAndLegacyDatabasesWithoutChangingCredentials() {
    for (var legacy : List.of(false, true)) {
      var catalog = legacy ? "authorizations_legacy" : "authorizations_fresh";
      try (var connection = DriverManager.getConnection(
          DATABASE.getJdbcUrl(), DATABASE.getUsername(), DATABASE.getPassword());
          var statement = connection.createStatement()) {
        statement.execute("CREATE DATABASE " + catalog);
      }

      try (var connection = DriverManager.getConnection(
          DATABASE.getJdbcUrl(), DATABASE.getUsername(), DATABASE.getPassword())) {
        connection.setCatalog(catalog);
        var database = DatabaseFactory.getInstance()
            .findCorrectDatabaseImplementation(new JdbcConnection(connection));
        try (var resources = new ClassLoaderResourceAccessor()) {
          if (legacy) {
            var before = new Liquibase("db/changelog-master.yaml", resources, database);
            before.getDatabaseChangeLog().getChangeSets()
                .removeIf(it -> it.getId().equals("20261111-authorization-groups"));
            before.update(new Contexts(), new LabelExpression());
          } else {
            new Liquibase("db/changelog-master.yaml", resources, database)
                .update(new Contexts(), new LabelExpression());
          }

          try (var statement = connection.createStatement()) {
            statement.execute("""
                INSERT INTO customers (project_id, entity_id, entity_version, customer_name)
                VALUES ('00000000-0000-0000-0001-000000000001',
                        '00000000-0000-0000-0009-000000000001', 0, 'Migration fixture')
                """);
            for (var table : List.of("secret", "secret_ex")) {
              statement.execute("""
                  INSERT INTO %s (customer_id, location, username, password, changed_who,
                                  changed_when, otp_secret, otp_recovery_keys)
                  VALUES ('00000000-0000-0000-0009-000000000001', 'Portal', 'user', 'password',
                          'operator@example.test', '2020-01-02T03:04:05', 'seed', 'recovery')
                  """.formatted(table));
            }
          }

          new Liquibase("db/changelog-master.yaml", resources, database)
              .update(new Contexts(), new LabelExpression());

          for (var table : List.of("secret", "secret_ex")) {
            try (var statement = connection.createStatement();
                var result = statement.executeQuery("""
                    SELECT location, username, password, changed_who,
                           CONVERT(varchar(19), changed_when, 126), otp_secret,
                           otp_recovery_keys, authorization_group FROM %s
                    """.formatted(table))) {
              var rows = new ArrayList<List<String>>();
              while (result.next()) {
                var row = new ArrayList<String>();
                for (var column = 1; column <= 8; column++) {
                  row.add(result.getString(column));
                }
                rows.add(row);
              }
              assertThat(rows).isEqualTo(List.of(List.of("Portal", "user", "password",
                  "operator@example.test", "2020-01-02T03:04:05", "seed", "recovery", "SINNET")));
            }
            try (var statement = connection.createStatement()) {
              assertThatThrownBy(() -> statement.executeUpdate(
                  "UPDATE " + table + " SET authorization_group = 'INVALID'"))
                  .isInstanceOf(SQLException.class);
            }
          }
        } finally {
          database.close();
        }
      }
    }
  }
}
