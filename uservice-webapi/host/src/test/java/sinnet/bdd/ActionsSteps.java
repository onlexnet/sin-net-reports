package sinnet.bdd;

import java.time.LocalDate;
import java.util.UUID;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;

import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import onlexnet.sinnet.webapi.test.AppApi;
import sinnet.grpc.CustomersGrpcService;
import sinnet.grpc.ProjectsGrpcFacade;
import sinnet.grpc.UsersGrpcService;

public class ActionsSteps {

  @Autowired
  ProjectsGrpcFacade projectsGrpc;

  @Autowired
  CustomersGrpcService customersGrpc;

  @Autowired
  UsersGrpcService usersGrpc;

  @Autowired
  TestRestTemplate restTemplate;

  String requestorEmail;
  AppApi appApi;

  @Before
  public void before() {
    requestorEmail = "email@" + UUID.randomUUID();
    appApi = new AppApi(restTemplate.getRootUri(), requestorEmail);
    Mockito.clearInvocations(projectsGrpc);
  }
  
  @When("Actions list query is send")
  public void actions_list_query_is_send() {
    var projectId = "my-project-id";
    var now = LocalDate.now();
    var from = now.minusDays(1);
    var to = now.plusDays(1);
    appApi.searchActions(projectId, from, to);
  }

  @Then("Actions list response is returned")
  public void actions_list_response_is_returned() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }
}
