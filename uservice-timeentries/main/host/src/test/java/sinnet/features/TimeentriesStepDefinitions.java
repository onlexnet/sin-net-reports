package sinnet.features;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TimeentriesStepDefinitions {

  private final TestApi testApi;

  @Given("a new project called {string}")
  public void a_new_project_called(String string) {
    // Write code here that turns the phrase above into concrete actions
    // throw new io.cucumber.java.PendingException();
  }

  @Given("an operator called {string}")
  public void an_operator_called(String string) {
    // Write code here that turns the phrase above into concrete actions
    // throw new io.cucumber.java.PendingException();
  }

  @When("operator called {string} creates new timeentry")
  public void operator_called_creates_new_timeentry(String string) {
    // Write code here that turns the phrase above into concrete actions
    // throw new io.cucumber.java.PendingException();
  }

  @Then("operation succeeded")
  public void operation_succeeded() {
    // Write code here that turns the phrase above into concrete actions
    // throw new io.cucumber.java.PendingException();
  }
}
