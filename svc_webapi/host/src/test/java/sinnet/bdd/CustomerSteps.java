package sinnet.bdd;

import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;

import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import onlexnet.sinnet.webapi.test.AppApi;
import sinnet.app.flow.request.CustomerGetResult;
import sinnet.app.flow.request.CustomerListResult;
import sinnet.app.ports.out.CustomersPortOut;
import sinnet.domain.models.CustomerEntry;
import sinnet.domain.models.CustomerSecretEx;
import sinnet.gql.models.CustomerSecretExGql;

public class CustomerSteps {

  @Autowired
  CustomersPortOut customersPortOut;

  @Autowired
  TestRestTemplate restTemplate;

  String requestorEmail;
  AppApi appApi;

  @Before
  public void before() {
    requestorEmail = "email@" + UUID.randomUUID();
    appApi = new AppApi(restTemplate.getRootUri(), requestorEmail);
    Mockito.clearInvocations(customersPortOut);
  }
  
  @When("Customer list request is send to backend")
  public void customer_list_request_is_send_to_backend() {
    var projectId = UUID.randomUUID();

    Mockito
      .when(customersPortOut.list(any()))
      .thenReturn(new CustomerListResult(List.of()));

    appApi.listCustomers(projectId);
  }

  @Then("Customer list result is verified")
  public void customer_list_result_is_verified() {
      // Write code here that turns the phrase above into concrete actions
      // throw new io.cucumber.java.PendingException();
  }

  @When("Customer read request is send to backend")
  public void customer_read_request_is_send_to_backend() {
    var projectId = UUID.randomUUID();
    var entityId = UUID.randomUUID();

    Mockito
      .when(customersPortOut.get(any()) )
      .thenReturn(new CustomerGetResult(
        new sinnet.domain.models.EntityId(projectId, entityId, 1L),
        new sinnet.domain.models.CustomerValue(
          new CustomerEntry(
            null,
            "my billing model",
            null,
            0,
            "my customer",
            null,
            null,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            null,
            false,
            null,
            null),
          List.of(),
          List.of(new CustomerSecretEx(
            "",
            "",
            "",
            "",
            "",
            "my secret",
            "my key1",
            null,
            null)),
          List.of())));

    var response = appApi.getCustomer(projectId, entityId).get();

    Assertions.assertThat(response.getData().getBillingModel()).isEqualTo("my billing model");
    Assertions.assertThat(response.getSecretsEx()).containsOnly(new CustomerSecretExGql()
        .setLocation("")
        .setUsername("")
        .setPassword("")
        .setEntityName("")
        .setEntityCode("")
          .setChangedWhen("?")
          .setChangedWho("?")
        .setOtpSecret("my secret")
        .setOtpRecoveryKeys("my key1"));
  }

  @Then("Customer read result is verified")
  public void customer_read_result_is_verified() {
      // Write code here that turns the phrase above into concrete actions
      // throw new io.cucumber.java.PendingException();
  }

}
