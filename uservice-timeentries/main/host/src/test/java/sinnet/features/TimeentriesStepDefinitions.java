package sinnet.features;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TimeentriesStepDefinitions {

  private final TestApi testApi;
  private ClientContext ctx;

  @Given("a new project called {string}")
  public void a_new_project_called(String projectAlias) {
    ctx = new ClientContext();
    testApi.notifyNewProject(ctx, projectAlias);
  }

  @Given("an operator called {string} assigned to project called {string}")
  public void an_operator_called_assigned_to(String operatorAlias, String projectAlias) {
      // Write code here that turns the phrase above into concrete actions
      throw new io.cucumber.java.PendingException("222");
  }

  @When("operator called {string} creates new timeentry")
  public void operator_called_creates_new_timeentry(String string) {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException("333");
  }

  @Then("operation succeeded")
  public void operation_succeeded() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException("444");
  }
}
