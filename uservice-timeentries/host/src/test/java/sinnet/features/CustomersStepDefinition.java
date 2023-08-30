package sinnet.features;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomersStepDefinition {

  private final TestApi testApi;

  @When("the operator creates new Customer")
  public void the_operator_creates_new_customer() {
    testApi.reserveCustomer();
  }

  @Then("the Customer is visible on the list of customers")
  public void the_customer_is_visible_on_the_list_of_customers() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

}
