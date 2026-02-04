package sinnet.bdd;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import onlexnet.sinnet.webapi.test.AppApi;
import sinnet.gql.api.CommonMapper;
import sinnet.gql.models.ServiceModelGql;
import sinnet.gql.models.ServicesSearchResultGql;
import sinnet.gql.models.SomeEntityGql;
import sinnet.grpc.common.EntityId;
import sinnet.grpc.timeentries.TimeEntryModel;
import sinnet.ports.timeentries.ActionsGrpcFacade;

public class ActionsSteps {

  @Autowired
  ActionsGrpcFacade actionsGrpcFacade;

  @LocalServerPort
  int serverPort;

  String requestorEmail;
  AppApi appApi;

  @Before
  public void before() {
    requestorEmail = "email@" + UUID.randomUUID();
    appApi = new AppApi(serverPort, requestorEmail);
    Mockito.clearInvocations(actionsGrpcFacade);
  }
  
  @When("Actions list query is send")
  public void actions_list_query_is_send() {

    var projectId = UUID.randomUUID();
    var now = LocalDate.now();
    var from = now.minusDays(1);
    var to = now.plusDays(1);

    Mockito
      .when(actionsGrpcFacade.searchInternal(projectId, from, to))
      .thenReturn(List.of(
        TimeEntryModel.newBuilder()
          .setDescription("desc1")
          .setWhenProvided(CommonMapper.toGrpc(now))
          .setDistance(1)
          .setDuration(2)
          .setServicemanName("my-serviceman")
          .setServicemanEmail("my-serviceman-email")
          .setEntityId(EntityId.newBuilder().setEntityId("entity-id").setEntityVersion(10).setProjectId("project-id"))
          .build()
       ));


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
          .setEntityId("entity-id")
          .setEntityVersion(10)
          .setProjectId("project-id")
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
      .thenReturn(new sinnet.domain.EntityId(projectId, entityId, entityVersion));


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

    var result = TimeEntryModel.newBuilder()
        .setCustomerId(customerId.toString())
        .setDescription("my-description")
        .setDistance(1)
        .setDuration(2)
        .setEntityId(EntityId.newBuilder().setEntityId(entityId.toString()).setEntityVersion(42).setProjectId(projectId.toString()))
        .setServicemanEmail("serviceman-email")
        .setServicemanName("serviceman-name")
        .setWhenProvided(CommonMapper.toGrpc(now))
        .build();
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
