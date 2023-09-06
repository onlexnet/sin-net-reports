package sinnet.bdd;

import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.UUID;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;

import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import onlexnet.sinnet.webapi.test.AppApi;
import sinnet.grpc.ActionsGrpcFacade;
import sinnet.grpc.CustomersGrpcFacade;
import sinnet.grpc.customers.ListReply;

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

}
