package sinnet.features;

import org.assertj.core.api.Assertions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import sinnet.models.ValName;

@RequiredArgsConstructor
public class TimeentriesStepDefinitions {

  private final TestApi testApi;
  private final ClientContext ctx;

  @Given("a new project called {projectAlias} created by {operatorAlias}")
  public void a_new_project_called(ValName projectAlias, ValName operatorAlias) {
    testApi.newProject(ctx, operatorAlias, projectAlias);
  }

  @Given("an operator called {operatorAlias} assigned to project called {projectAlias}")
  public void an_operator_called_alias1_assigned_toproject_called_alias2(ValName operatorAlias, ValName projectAlias) {
    testApi.assignOperator(ctx, operatorAlias, projectAlias);
  }

  @When("{operatorAlias} creates new timeentry for {projectAlias}")
  public void operator_called_creates_new_timeentry(ValName operatorAlias, ValName projectAlias) {
    testApi.createEntry(ctx, operatorAlias, projectAlias);
  }

  @Then("operation succeeded")
  public void operation_succeeded() {
    // noe checks yet
  }

  @Then("the new timeentry is visible on the {projectAlias}")
  public void the_new_timeentry_is_visible_on_the_projectAlias(ValName projectAlias) {
    var latestTimeentryId = ctx.latestTimeentryId();
    var timeentryCtx = ctx.known().timeentries().get(latestTimeentryId);
    var items = testApi.listTimeentries(ctx, projectAlias, timeentryCtx.when());
    Assertions.assertThat(items).contains(latestTimeentryId);
  }
}
