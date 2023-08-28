package sinnet.bdd;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;

import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import onlexnet.sinnet.webapi.test.AppApi;
import sinnet.gql.models.ServiceModelGql;
import sinnet.gql.models.ServicesSearchResultGql;
import sinnet.grpc.ActionsGrpcFacade;
import sinnet.grpc.CustomersGrpcService;
import sinnet.grpc.ProjectsGrpcFacade;
import sinnet.grpc.UsersGrpcService;

public class ActionsSteps {

  @Autowired
  ActionsGrpcFacade actionsGrpcFacade;

  @Autowired
  TestRestTemplate restTemplate;

  String requestorEmail;
  AppApi appApi;

  @Before
  public void before() {
    requestorEmail = "email@" + UUID.randomUUID();
    appApi = new AppApi(restTemplate.getRootUri(), requestorEmail);
    Mockito.clearInvocations(actionsGrpcFacade);
  }
  
  @When("Actions list query is send")
  public void actions_list_query_is_send() {

    var projectId = UUID.randomUUID();
    var now = LocalDate.now();
    var from = now.minusDays(1);
    var to = now.plusDays(1);

    Mockito
      .when(actionsGrpcFacade.search(eq(projectId), eq(from), eq(to)))
      .thenReturn(List.of("a"));


    var actual = appApi.searchActions(projectId, from, to);


    var expected = new ServicesSearchResultGql()
        .setItems(List.of(
          new ServiceModelGql().setDescription("aaaa")
        ));

    Assertions.assertThat(actual).isEqualTo(expected);
  }

  @Then("Actions list response is returned")
  public void actions_list_response_is_returned() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }
}
