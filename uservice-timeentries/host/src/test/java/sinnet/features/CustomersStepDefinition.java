package sinnet.features;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomersStepDefinition {

  private final TestApi testApi;
  private final ClientContext ctx;

  @When("the Operator creates a new customer")
  public void the_operator_creates_a_new_customer() {
    testApi.reserveCustomer(ctx);
    testApi.updateCustomer(ctx, "new-name");
  }

  @Then("the Operator is able to change the name of the Customer")
  public void the_operator_is_able_to_change_the_name_of_the_customer() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

}
