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
import sinnet.gql.models.CustomerSecretExGql;
import sinnet.grpc.CustomersGrpcFacade;
import sinnet.grpc.customers.CustomerModel;
import sinnet.grpc.customers.CustomerSecretEx;
import sinnet.grpc.customers.CustomerValue;
import sinnet.grpc.customers.GetReply;
import sinnet.grpc.customers.ListReply;
import sinnet.grpc.customers.LocalDateTime;
import sinnet.grpc.customers.Totp;
import sinnet.otp.OtpGeneratorTests;

public class CustomerSteps {

  @Autowired
  CustomersGrpcFacade customersGrpc;

  @Autowired
  TestRestTemplate restTemplate;

  String requestorEmail;
  AppApi appApi;

  @Before
  public void before() {
    requestorEmail = "email@" + UUID.randomUUID();
    appApi = new AppApi(restTemplate.getRootUri(), requestorEmail);
    Mockito.clearInvocations(customersGrpc);
  }
  
  @When("Customer list request is send to backend")
  public void customer_list_request_is_send_to_backend() {
    var projectId = UUID.randomUUID();

    Mockito
      .when(customersGrpc.list(any()))
      .thenReturn(ListReply
        .newBuilder()
        .addAllCustomers(List.of()).build());

    appApi.listCustomers(projectId);
  }

  @Then("Customer list result is verified")
  public void customer_list_result_is_verified() {
      // Write code here that turns the phrase above into concrete actions
      // throw new io.cucumber.java.PendingException();
  }

  @When("Customer read request is send to backend")
  public void customer_read_request_is_send_to_backend() {
    Mockito
      .when(customersGrpc.get(any()) )
      .thenReturn(GetReply
        .newBuilder()
        .setModel(CustomerModel.newBuilder()
          .setValue(CustomerValue.newBuilder()
            .setBillingModel("my billing model"))
          .addSecretEx(CustomerSecretEx.newBuilder()
            .setChangedWhen(LocalDateTime.newBuilder().setYear(2001).setMonth(2).setDay(3).setHour(4).setMinute(5))
            .setTotp(Totp.newBuilder().setCounter(30).setSecret(OtpGeneratorTests.exampleSecret))))
            .build());
    
    var projectId = UUID.randomUUID();
    var entityId = UUID.randomUUID();
    var response = appApi.getCustomer(projectId, entityId).get();

    Assertions.assertThat(response.getData().getBillingModel()).isEqualTo("my billing model");
    Assertions.assertThat(response.getSecretsEx()).containsOnly(new CustomerSecretExGql()
        .setLocation("")
        .setUsername("")
        .setPassword("")
        .setEntityName("")
        .setEntityCode("")
        .setChangedWhen("2001-02-03T04:05:00")
        .setChangedWho("")
        .setTotp(OtpGeneratorTests.expectedCode));
  }

  @Then("Customer read result is verified")
  public void customer_read_result_is_verified() {
      // Write code here that turns the phrase above into concrete actions
      // throw new io.cucumber.java.PendingException();
  }

}
