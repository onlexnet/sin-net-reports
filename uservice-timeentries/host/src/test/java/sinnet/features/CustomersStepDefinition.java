package sinnet.features;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import sinnet.models.ValName;

@RequiredArgsConstructor
public class CustomersStepDefinition {

  private final TestApi testApi;
  private final ClientContext ctx;

  private ValName singleCustomer = ValName.of("example customer");

  @When("{operatorAlias} creates a new customer")
  public void the_operator_creates_a_new_customer(ValName operatorAlias) {
    testApi.reserveCustomer(ctx, singleCustomer);
  }

  @Then("the Operator is able to change the name of the Customer")
  public void the_operator_is_able_to_change_the_name_of_the_customer() {
    testApi.updateCustomer(ctx, singleCustomer, "new-name-1");

    testApi.customerExists(ctx, "new-name-1");
    testApi.updateCustomer(ctx, singleCustomer, "new-name-2");

    testApi.customerExists(ctx, "new-name-2");
  }

}
