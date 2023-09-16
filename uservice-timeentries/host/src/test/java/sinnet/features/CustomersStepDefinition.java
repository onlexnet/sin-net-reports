package sinnet.features;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import sinnet.models.CustomerModel;
import sinnet.models.CustomerValue;
import sinnet.models.ValName;

@RequiredArgsConstructor
public class CustomersStepDefinition {

  private final TestApi testApi;
  private final ClientContext ctx;

  private ValName singleCustomer = ValName.of("example customer");

  @When("{operatorAlias} creates a new customer")
  public void the_operator_creates_a_new_customer(ValName operatorAlias) {
    testApi.reserveCustomer(ctx, operatorAlias);
  }

  @Then("{operatorAlias} is able to change the name of the Customer")
  public void the_operator_is_able_to_change_the_name_of_the_customer(ValName operatorAlias) {
    testApi.updateCustomer(ctx, singleCustomer, new CustomerValue().customerName(ValName.of("new-name-1")));

    testApi.customerExists(ctx, operatorAlias, "new-name-1");
    testApi.updateCustomer(ctx, singleCustomer, new CustomerValue().customerName(ValName.of("new-name-2")));

    testApi.customerExists(ctx, operatorAlias, "new-name-2");
  }

  @Then("{operatorAlias} is able to change all properties of the Customer")
  public void operator1_is_able_to_change_all_properties_of_the_c_ustomer(ValName operatorAlias) {
    var customerValue = new CustomerValue().customerName(ValName.of("new-name-1"));
    testApi.updateCustomer(ctx, singleCustomer, customerValue);
    testApi

  }

}
