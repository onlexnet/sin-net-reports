package sinnet;

import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import io.quarkus.liquibase.LiquibaseFactory;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import liquibase.Liquibase;
import liquibase.changelog.ChangeSetStatus;
import lombok.Cleanup;
import lombok.SneakyThrows;

@QuarkusTest
@QuarkusTestResource(InitDbTestResource.class)
@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
public class RollbackTest {

  @Inject
  LiquibaseFactory liquibaseFactory;

  @SneakyThrows
  public void checkMigration() {
    // Get the list of liquibase change set statuses
    @Cleanup
    Liquibase liquibase = liquibaseFactory.createLiquibase();
    List<ChangeSetStatus> status = liquibase.getChangeSetStatuses(liquibaseFactory.createContexts(), liquibaseFactory.createLabels());
  }

  // We need rollback for database operations so that rollback procedure is required for all liquibase changesets.
  @Test
  @SneakyThrows
  void should_rollback_all_changes() {
    @Cleanup
    var liquibase = liquibaseFactory.createLiquibase();
    var currentContext = liquibaseFactory.createContexts();
    var initialTag = "v0"; // tag in database created before any changeset
    liquibase.rollback(initialTag, currentContext);
  }

}
