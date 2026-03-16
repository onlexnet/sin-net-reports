package sinnet.bdd;

import java.time.LocalDate;
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
import sinnet.app.ports.out.TimeentriesPortOut;
import sinnet.domain.models.TimeEntry;
import sinnet.gql.models.ServiceModelGql;
import sinnet.gql.models.ServicesSearchResultGql;
import sinnet.gql.models.SomeEntityGql;

public class TimeentriesSteps {

  @Autowired
  TimeentriesPortOut actionsGrpcFacade;

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
    var entityId = UUID.randomUUID();

    Mockito
      .when(actionsGrpcFacade.searchInternal(projectId, from, to))
      .thenReturn(List.of(
        new TimeEntry(new sinnet.domain.models.EntityId(projectId, entityId, 10),
        null, "desc1", 1, 2, "my-serviceman-email", "my-serviceman", now)));


    var actual = appApi.searchActions(projectId, from, to).get();


    var expected = new ServicesSearchResultGql()
        .setItems(List.of(
          new ServiceModelGql()
          .setDescription("desc1")
          .setWhenProvided(now)
          .setDistance(1)
          .setDuration(2)
          .setServicemanName("my-serviceman")
          .setServicemanEmail("my-serviceman-email")
          .setEntityId(entityId.toString())
          .setEntityVersion(10)
          .setProjectId(projectId.toString())
        ));

    Assertions.assertThat(actual).isEqualTo(expected);
  }

  @Then("Actions list response is returned")
  public void actions_list_response_is_returned() {
  }

  @When("Actions create command is send")
  public void actions_create_command_is_send() {

    var projectId = UUID.randomUUID();
    var when = LocalDate.now();
    var entityId = UUID.randomUUID();
    var entityVersion = 42L;
    Mockito
      .when(actionsGrpcFacade.newAction(requestorEmail, projectId, when))
      .thenReturn(new sinnet.domain.models.EntityId(projectId, entityId, entityVersion));


    var actual = appApi.newAction(projectId.toString(), when).get();

    Assertions.assertThat(actual).isEqualTo(new SomeEntityGql().setProjectId(projectId.toString()).setEntityId(entityId.toString()).setEntityVersion(entityVersion));
  }

  @Then("Actions create result is returned")
  public void actions_create_result_is_returned() {
  }

  @When("Actions get query is sent")
  public void actions_get_query_is_sent() {

    var projectId = UUID.randomUUID();
    var entityId = UUID.randomUUID();
    var customerId = UUID.randomUUID();
    var now = LocalDate.now();

    var result = new TimeEntry(new sinnet.domain.models.EntityId(projectId, entityId, 42), customerId.toString(), "my-description", 1, 2, "serviceman-email", "serviceman-name", now);
    Mockito
      .when(actionsGrpcFacade.getActionInternal(projectId, entityId))
      .thenReturn(result);

      var actual = appApi.getAction(projectId.toString(), entityId.toString()).get();

      var expected = new ServiceModelGql()
          .setDescription("my-description")
          .setDistance(1)
          .setDuration(2)
          .setEntityId(entityId.toString())
          .setEntityVersion(42)
          .setProjectId(projectId.toString())
          .setServicemanEmail("serviceman-email")
          .setServicemanName("serviceman-name")
          .setWhenProvided(now);

      Assertions.assertThat(actual).isEqualTo(expected);
  }
  
  @Then("Actions get query is returned")
  public void actions_get_query_is_returned() {
  }

}
