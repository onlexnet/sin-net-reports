package sinnet.bdd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;

import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import onlexnet.sinnet.webapi.test.AppApi;
import sinnet.app.flow.request.CustomerUpdateCommand;
import sinnet.app.lib.TimeProvider;
import sinnet.app.ports.out.CustomersPortOut;
import sinnet.app.ports.out.ProjectsPortOut;
import sinnet.app.ports.out.ProjectsPortOut.StatsResult;
import sinnet.app.ports.out.UsersServicePortOut;
import sinnet.domain.models.CustomerEntry;
import sinnet.domain.models.CustomerValue;
import sinnet.domain.models.Email;
import sinnet.domain.models.Project;
import sinnet.domain.models.ProjectId;
import sinnet.gql.api.CommonMapper;
import sinnet.gql.models.CustomerInput;
import sinnet.gql.models.CustomerSecretExInput;
import sinnet.gql.models.CustomerSecretInput;
import sinnet.gql.models.EntityGql;
import sinnet.gql.models.ProjectEntityGql;
import sinnet.gql.models.SomeEntityGql;
import sinnet.gql.models.UserGql;
import sinnet.grpc.common.EntityId;
import sinnet.grpc.customers.UpdateResult;
import sinnet.grpc.users.UsersSearchModel;

public class StepDefinitions {

  @Autowired
  ProjectsPortOut projects;

  @Autowired
  CustomersPortOut customersGrpc;

  @Autowired
  UsersServicePortOut usersGrpc;

  @Autowired
  TestRestTemplate restTemplate;

  @Autowired
  CommonMapper commonMapper;

  @Autowired
  TimeProvider timeProvider;

  String requestorEmail;
  AppApi appApi;

  @Before
  public void before() {
    requestorEmail = "email@" + UUID.randomUUID();
    appApi = new AppApi(restTemplate.getRootUri(), requestorEmail);
    Mockito.clearInvocations(projects);
  }

  @When("user is requesting list of projects")
  public void user_is_requesting_list_of_projects() {

    var projectId = UUID.randomUUID();
    var result = new Project(projectId, 2L, "my name");
    Mockito
        .when(projects.list(eq(requestorEmail)))
        .thenReturn(List.of(result));

    appApi.findProjectByName("spring-framework")
        .hasSizeGreaterThan(0);

  }

  @Then("Response is returned")
  public void response_is_returned() {
  }

  @Then("Project uservice is requested")
  public void Project_uservice_is_requested() {
    // Write code here that turns the phrase above into concrete actions
  }

  ProjectEntityGql expectedCreatedProject;
  ProjectEntityGql lastlyCreatedProject;
  Integer expectedNumberOfProjects;

  @When("a project is saved")
  public void project_is_saved() {
    var projectId = UUID.randomUUID();
    var projectNewName = "projectname-" + UUID.randomUUID();

    var projectId1 = new ProjectId(projectId, 1L);
    Mockito
        .when(projects.create(requestorEmail))
        .thenReturn(projectId1);
    var projectId2 = new ProjectId(projectId, 2L);
    Mockito
        .when(
            projects.update(eq(requestorEmail), eq(projectId1), eq(projectNewName), eq(requestorEmail), eq(List.of())))
        .thenReturn(projectId2);
    var saveResult = appApi.createProject(projectNewName);

    lastlyCreatedProject = saveResult.get();
    expectedCreatedProject = new ProjectEntityGql(
        new SomeEntityGql()
            .setEntityId(projectId.toString())
            .setEntityVersion(2L)
            .setProjectId(projectId.toString()),
        projectNewName);
  }

  @Then("operation result is returned")
  public void operation_result_is_returned() {
    assertThat(lastlyCreatedProject)
        .isEqualTo(expectedCreatedProject);
  }

  @When("userstats request is send to backend")
  public void userstats_request_is_send_to_backend() {

    expectedNumberOfProjects = new Random().nextInt();
    Mockito
        .when(projects.userStats(requestorEmail))
        .thenReturn(new StatsResult(expectedNumberOfProjects));
  }

  @Then("userstats are returned")
  public void userstats_are_returned() {
    var numberOfProjects = appApi.numberOfProjects().get();

    Mockito
        .verify(projects)
        .userStats(requestorEmail);

    assertThat(numberOfProjects)
        .isEqualTo(expectedNumberOfProjects);
  }

  Runnable reserveValidation;

  @When("Customer creation request is send to backend")
  public void Customer_creation_request_is_send_to_backend() {
    var projectId = UUID.randomUUID().toString();

    var projectVersion = 1L;
    var expectedCmd = sinnet.grpc.customers.ReserveRequest.newBuilder()
        .setProjectId(projectId)
        .build();
    var expectedResult = sinnet.grpc.customers.ReserveReply.newBuilder()
        .setEntityId(EntityId.newBuilder()
            .setEntityId(projectId)
            .setEntityVersion(projectVersion)
            .build())
        .build();

    Mockito
      .when(customersGrpc.reserve(expectedCmd))
      .thenReturn(expectedResult);

    reserveValidation = () -> {
      var reserveCustomerResult = appApi.reserveCustomer(projectId).get();;
      assertThat(reserveCustomerResult.getEntityId()).isEqualTo(projectId);
      assertThat(reserveCustomerResult.getEntityVersion()).isEqualTo(projectVersion);
    };
  }

  @Then("Customer creation result is verified")
  public void Customer_creation_result_is_returned() {
      reserveValidation.run();
      reserveValidation = null;
  }

  @When("Users list query is send")
  public void Users_list_query_is_send() {
    var projectId = UUID.randomUUID();

    var reply = sinnet.grpc.users.SearchReply.newBuilder()
        .addItems(UsersSearchModel.newBuilder()
          .setEmail("my email")
          .setEntityId("my entity id"))
        .build();

    Mockito
      .when(usersGrpc.search(projectId, Email.of(requestorEmail)))
      .thenReturn(reply);

    userListValidation = () -> {
      var response = appApi.searchUsers(projectId).get();
      assertThat(response)
        .containsOnly(new UserGql("my email", "my entity id"));
    };

  }

  Runnable userListValidation;

  @Then("Users list response is returned")
  public void Users_list_response_is_returned() {
    userListValidation.run();
    userListValidation = null;
  }

  @When("Customer save request is send to backend")
  public void customer_save_request_is_send_to_backend() {
    var projectId = UUID.randomUUID();
    var projectIdStr = projectId.toString();
    var entityId = UUID.randomUUID();
    var entityIdStr = entityId.toString();

    var argumentCaptor = ArgumentCaptor.forClass(CustomerUpdateCommand.class);
    Mockito
      .when(customersGrpc.update(argumentCaptor.capture()))
      .thenReturn(UpdateResult.newBuilder()
        .setEntityId(EntityId.newBuilder().setProjectId(projectIdStr).setEntityId(entityIdStr).setEntityVersion(42L)
          .setProjectId(projectId.toString())
          .setEntityId(entityId.toString())
          .setEntityVersion(43))
          .build());

      var secret = new CustomerSecretInput()
          .setLocation("location 1")
          .setPassword("password 1")
          .setUsername("username 1")
          .setOtpSecret("my secret 1")
          .setOtpRecoveryKeys("my key 1");
      var secretExt = new CustomerSecretExInput()
        .setEntityCode("entity code 2")
        .setEntityName("entity name 2")
        .setLocation("location 2")
        .setPassword("password 2")
        .setUsername("username 2")
        .setOtpSecret("my secret 2")
        .setOtpRecoveryKeys("my key 2");
    var actual = appApi.saveCustomer(
      projectId,
      new SomeEntityGql().setProjectId(projectIdStr).setEntityId(entityIdStr).setEntityVersion(42L),
      new CustomerInput(),
      List.of(secret),
      List.of(secretExt),
      List.of()).get();

    var expectedResult = new SomeEntityGql().setProjectId(projectIdStr).setEntityId(entityId.toString()).setEntityVersion(43L);
    Assertions.assertThat(actual).isEqualTo(expectedResult);

    var expectedEntry = new CustomerEntry(
        null,
        null,
        null,
      0,
        null,
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
        null);

    var expectedCommand = new CustomerUpdateCommand(
        new sinnet.domain.models.UserToken(projectId, requestorEmail),
        new EntityGql(projectIdStr, entityIdStr, 42),
        new CustomerValue(
          expectedEntry,
          List.of(secret),
          List.of(secretExt),
          List.of()),
          timeProvider.now(),
          requestorEmail);
    Assertions.assertThat(argumentCaptor.getValue())
        .isEqualTo(expectedCommand);
  }

  @Then("Customer save result is verified")
  public void customer_save_result_is_verified() {
  }

  @When("Actions export query is send")
  public void actions_export_query_is_send() {
    var projectId = UUID.randomUUID();
    var projectIdStr = projectId.toString();
    var year = 2024;
    var month = 12;

    var file = appApi.downloadFile(projectIdStr, year, month).get();
    Assertions.assertThat(file).isNotNull();
    Assertions.assertThat(file.getFileName()).isEqualTo(String.format("export_%d-%02d.xlsx", year, month));
    Assertions.assertThat(file.getContentType()).isEqualTo("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    Assertions.assertThat(file.getContent()).isNotEmpty();
  }

  @Then("Actions export result is returned")
  public void actions_export_result_is_returned() {

  }

}

