package sinnet.features;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import sinnet.models.ValName;

@RequiredArgsConstructor
public class TimeentriesStepDefinitions {

  private final TestApi testApi;
  private ClientContext ctx;

  @Given("a new project called {projectName}")
  public void a_new_project_called(ValName projectAlias) {
    ctx = new ClientContext();
    testApi.notifyNewProject(ctx, projectAlias);
  }

  @Given("an operator called {operatorName} assigned to project called {projectName}")
  public void an_operator_called_alias1_assigned_toproject_called_alias2(ValName operatorAlias, ValName projectAlias) {
    ctx.getOperatorId(operatorAlias, true);
    ctx.setProjectId(projectAlias);
    testApi.assignOperator(ctx);
  }

  @When("the operator creates new timeentry")
  public void operator_called_creates_new_timeentry() {
    testApi.createEntry(ctx);
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException("333");
  }

  @Then("operation succeeded")
  public void operation_succeeded() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException("444");
  }
}
