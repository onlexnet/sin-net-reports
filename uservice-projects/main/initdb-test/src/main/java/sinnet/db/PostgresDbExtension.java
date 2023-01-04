package sinnet.db;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.google.common.util.concurrent.Runnables;

import lombok.extern.slf4j.Slf4j;

/**
 * Reusable extension to start local PostgreSQL instance for µservices based on
 * its databases.
 */
@Slf4j
public final class PostgresDbExtension implements BeforeAllCallback,
    AfterAllCallback,
    ExecutionCondition {

  private AutoCloseable disposer = Runnables::doNothing;
  private boolean initializationFailed = false;

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    initializationFailed = true; // just in case unsuccessful initialization
    var dbRunner = new PostgresDbRunner();
    disposer = dbRunner.start();
    initializationFailed = false;
  }

  @Override
  public void afterAll(ExtensionContext context) throws Exception {
    disposer.close();
  }

  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
    return !initializationFailed
        ? ConditionEvaluationResult.enabled(null)
        : ConditionEvaluationResult.disabled("Database initialization issue");
  }

}
