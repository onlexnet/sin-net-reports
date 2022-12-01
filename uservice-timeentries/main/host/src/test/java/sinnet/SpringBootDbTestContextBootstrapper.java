package sinnet;

import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.test.context.TestContext;

import sinnet.db.PostgresDbRunner;

/**
 * For Cucumber we would like to run 3 things in the same time:
 * Cucumber, SpringBoot, Database
 * The main technical issue is, Cucumber+Spring integration does not honor @ExtendWith, especially when we use
 * @ExtendWith(PostgresDbExtension.class) to run database before tests.
 * As w can't find any way how to run them togetger, th bootstrapper runs Db and
 * Spring in one piece here.
 */
public final class SpringBootDbTestContextBootstrapper extends SpringBootTestContextBootstrapper {

  @Override
  public TestContext buildTestContext() {
    var dbRunner = new PostgresDbRunner();
    var disposer = dbRunner.start();
    var testContext = super.buildTestContext();
    var appContext = (AbstractApplicationContext) testContext.getApplicationContext();
    appContext.addApplicationListener((ContextClosedEvent e) -> disposer.close());
    return testContext;
  }

}
