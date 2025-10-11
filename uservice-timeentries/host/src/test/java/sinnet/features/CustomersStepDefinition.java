package sinnet.features;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.instancio.Select;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import sinnet.grpc.CustomerSandboxTest;
import sinnet.grpc.customers.CustomerModelMapper;
import sinnet.models.CustomerModel;
import sinnet.models.CustomerSecret;
import sinnet.models.CustomerSecretEx;
import sinnet.models.CustomerValue;
import sinnet.models.ValName;

@RequiredArgsConstructor
public class CustomersStepDefinition {

  private final TestApi testApi;
  private final ClientContext ctx;

  private ValName singleCustomer = ValName.of("example customer");

  @When("{operatorAlias} creates a new customer named {customerAlias}")
  public void the_operator_creates_a_new_customer(ValName operatorAlias, ValName customerAlias) {
    testApi.reserveCustomer(ctx, operatorAlias, customerAlias);
    testApi.updateReservedCustomer(ctx, customerAlias, new CustomerValue().setCustomerName(ValName.of("name: " + customerAlias.getValue())), List.of(), List.of());
  }

  @Then("{operatorAlias} is able to change the name of the Customer")
  public void the_operator_is_able_to_change_the_name_of_the_customer(ValName operatorAlias) {
    testApi.updateReservedCustomer(ctx, singleCustomer, new CustomerValue().setCustomerName(ValName.of("new-name-1")), List.of(), List.of());

    testApi.customerExists(ctx, operatorAlias, "new-name-1");
    testApi.updateReservedCustomer(ctx, singleCustomer, new CustomerValue().setCustomerName(ValName.of("new-name-2")), List.of(), List.of());

    testApi.customerExists(ctx, operatorAlias, "new-name-2");
  }

  @Then("{operatorAlias} is able to change all properties of the Customer")
  public void operator1_is_able_to_change_all_properties_of_the_c_ustomer(ValName operatorAlias) {
    var customerValue = new CustomerValue()
        .setCustomerName(ValName.of("new-name-1"))
        .setBillingModel("my-billing-model");
    var secrets = new CustomerSecret()
        .setChangedWhen(LocalDateTime.of(2001,2,3,4,5,6))
        .setOtpSecret("my secret1")
        .setOtpRecoveryKeys("key1");
    var secretExt = new CustomerSecretEx()
        .setChangedWhen(LocalDateTime.of(2001,2,3,4,5,6))
        .setOtpSecret("my secret2")
        .setOtpRecoveryKeys("key2");
    testApi.updateReservedCustomer(ctx, singleCustomer, customerValue, List.of(secrets), List.of(secretExt));
    var customer = testApi.getCustomer(ctx, singleCustomer, operatorAlias);

    Assertions.assertThat(customer.getValue().getCustomerName().getValue()).isEqualTo("new-name-1");
    Assertions.assertThat(customer.getValue().getBillingModel()).isEqualTo("my-billing-model");

    Assertions.assertThat(customer.getSecrets()).isNotEmpty();
    var actualSecret = customer.getSecrets().get(0);
    Assertions.assertThat(actualSecret.getOtpSecret()).isEqualTo("my secret1");
    Assertions.assertThat(actualSecret.getOtpRecoveryKeys()).isEqualTo("key1");

    Assertions.assertThat(customer.getSecretsEx()).isNotEmpty();
    var actualSecretEx = customer.getSecretsEx().get(0);
    Assertions.assertThat(actualSecretEx.getOtpSecret()).isEqualTo("my secret2");
    Assertions.assertThat(actualSecretEx.getOtpRecoveryKeys()).isEqualTo("key2");

  }

  @When("{operatorAlias} creates a new fullcustomer named {customerAlias}")
  public void operator1_creates_a_new_fullcustomer_named_customer1(ValName operatorAlias, ValName customerAlias) {
      var customer = Instancio.of(CustomerModel.class)
          .generate(Select.all(LocalDateTime.class), gen -> gen.temporal().localDateTime().future().truncatedTo(ChronoUnit.SECONDS))
          .create();
      testApi.reserveCustomer(ctx, operatorAlias, customerAlias);
      testApi.updateReservedCustomer(ctx, customerAlias, customer.getValue(), customer.getContacts(), customer.getSecrets(), customer.getSecretsEx());
  }

  @Then("{operatorAlias} is able to list {customerAlias}")
  public void operator1_is_able_to_list_customer1(ValName operatorAlias, ValName customerAlias) {
    var expectedDto = ctx.known().customers().get(customerAlias)._2;
    var expected = CustomerModelMapper.INSTANCE.fromDto(expectedDto);

    var customersInDb = testApi.getAllCustomersData(ctx, operatorAlias);
    var customerInDb = customersInDb.stream().filter(it -> it.getValue().getCustomerName().equals(expected.getValue().getCustomerName().getValue())).findFirst().get();
    var actual = CustomerModelMapper.INSTANCE.fromDto(customerInDb);

    actual.setId(expected.getId());
    CustomerSandboxTest.sortSubcollections(actual);
    CustomerSandboxTest.sortSubcollections(expected);

    for (int i = 0; i < expected.getSecrets().size(); i++) {
      expected.getSecrets().get(i).setChangedWhen(actual.getSecrets().get(i).getChangedWhen());
      expected.getSecrets().get(i).setChangedWho(actual.getSecrets().get(i).getChangedWho());
    }
    for (int i = 0; i < expected.getSecretsEx().size(); i++) {
      expected.getSecretsEx().get(i).setChangedWhen(actual.getSecretsEx().get(i).getChangedWhen());
      expected.getSecretsEx().get(i).setChangedWho(actual.getSecretsEx().get(i).getChangedWho());
    }

    Assertions.assertThat(actual).isEqualTo(expected);
  }
}
