package sinnet;

import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.test.context.TestContext;

import lombok.extern.slf4j.Slf4j;
import sinnet.db.SqlServerDbRunner;

/**
 * For Cucumber we would like to run 3 things in the same time:
 * Cucumber, SpringBoot, Database
 * The main technical issue is, Cucumber+Spring integration does not honor @ExtendWith, especially when we use
 * @ExtendWith(SqlServerDbExtension.class) to run database before tests.
 * As w can't find any way how to run them together, th bootstrapper runs Db and
 * Spring in one piece here.
 * the issue we trying solve: https://stackoverflow.com/questions/74431287/junit-5-how-to-use-extensions-with-a-test-suite
 */
@Slf4j
public final class SpringBootDbTestContextBootstrapper extends SpringBootTestContextBootstrapper {

  public SpringBootDbTestContextBootstrapper() {
    super();
    log.error("SPARTA1");
  }
  
  @Override
  public TestContext buildTestContext() {
    var dbRunner = new SqlServerDbRunner();
    var disposer = dbRunner.start();
    var testContext = super.buildTestContext();
    var appContext = (AbstractApplicationContext) testContext.getApplicationContext();
    appContext.addApplicationListener((ContextClosedEvent e) -> disposer.close());
    return testContext;
  }



}
