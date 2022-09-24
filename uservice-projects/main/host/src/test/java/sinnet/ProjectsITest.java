package sinnet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.test.junit.QuarkusTest;
import io.vavr.Function1;
import lombok.SneakyThrows;
import lombok.val;
import sinnet.grpc.projects.CreateRequest;
import sinnet.grpc.projects.ListRequest;
import sinnet.grpc.projects.Project;
import sinnet.grpc.projects.ProjectId;
import sinnet.grpc.projects.ProjectModel;
import sinnet.grpc.projects.ProjectsGrpc;
import sinnet.grpc.projects.RemoveCommand;
import sinnet.grpc.projects.UpdateCommand;
import sinnet.grpc.projects.UserToken;

import static io.restassured.RestAssured.given;

@QuarkusTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ProjectsITest {

  @Inject
  @GrpcClient
  ProjectsGrpc.ProjectsBlockingStub self;

  private static Function1<Project, String> mapProjectToName = Function1.of(Project::getModel)
      .andThen(ProjectModel::getName);

  
  @Nested
  class ShouldValidate {

    final int maximumSizeOfUserEmail = 50;

    @Test
    void too_long_name() {
      var tooLongEmail = RandomStringUtils.randomAlphanumeric(maximumSizeOfUserEmail + 1);
      Assertions
        .assertThatCode(() -> ProjectsITest.this.create(tooLongEmail))
        .isInstanceOfSatisfying(StatusRuntimeException.class,
          ex -> assertThat(ex.getStatus().getCode()).isEqualTo(Status.FAILED_PRECONDITION.getCode()));
    }

    @Test
    void maximum_name() {
      var longEmail = RandomStringUtils.randomAlphanumeric(maximumSizeOfUserEmail);
      Assertions
        .assertThatCode(() -> ProjectsITest.this.create(longEmail))
        .doesNotThrowAnyException();
    }
  }
  
  @Test
  void should_create() {
    var ownerName = generateOwnerEmail();
    var projectId = create(ownerName);

    var updatedId = update(projectId, "my name", ownerName);
    assertThat(updatedId.getEId()).isEqualTo(projectId.getEId());
    assertThat(updatedId.getETag()).isEqualTo(projectId.getETag() + 1);

    var projectsOfOwner = listOfProjects(ownerName, Function1.identity());
    assertThat(projectsOfOwner)
        .as("List of projects contains just created entry")
        .isNotEmpty();

    var item0 = projectsOfOwner.get(0);
    var item0Id = item0.getId();
    var expected0id = item0Id.toBuilder().setETag(projectId.getETag() + 1).build();
    var expected0model = item0.getModel();
    var expected = Project.newBuilder()
        .setId(expected0id)
        .setModel(expected0model)
        .build();
    assertThat(projectsOfOwner).contains(expected);
  }

  @Test
  void should_limit_number_of_free_projects() {
    var ownerName = generateOwnerEmail();

    // lets create 3 free projects
    update(create(ownerName), "my name", ownerName);
    update(create(ownerName), "my name", ownerName);
    update(create(ownerName), "my name", ownerName);

    Assertions
        .assertThatCode(() -> update(create(ownerName), "my name", ownerName))
        .isInstanceOfSatisfying(StatusRuntimeException.class,
            ex -> assertThat(ex.getStatus().getCode()).isEqualTo(Status.RESOURCE_EXHAUSTED.getCode()));
  }

  @Test
  void should_remove() {
    var ownerName = generateOwnerEmail();
    var projectId = create(ownerName);

    update(projectId, "my name", ownerName);
    remove(projectId, ownerName);

    var projectsOfOwner = listOfProjects(ownerName, Function1.identity());
    assertThat(projectsOfOwner)
        .as("List of projects does not contain just deleted project")
        .isEmpty();
  }

  @Test
  public void should_update() {
    var ownerEmail = generateOwnerEmail();
    
    var projectId = create(ownerEmail);

    var updateCmd = newUpdateCommand(projectId, "my name", ownerEmail, ownerEmail);
    var updateResult = self.update(updateCmd);

    var newOwnerEmail = generateOwnerEmail();
    updateCmd = newUpdateCommand(updateResult.getEntityId(), "my new name", newOwnerEmail, ownerEmail);
    self.update(updateCmd);

    var projectsOfOwner = listOfProjects(ownerEmail, mapProjectToName);
    assertThat(projectsOfOwner).isEmpty();

    var projectsOfNewOwner = listOfProjects(newOwnerEmail, mapProjectToName);
    assertThat(projectsOfNewOwner).containsExactly("my new name");
  }

  @Test
  void should_reject_stale_updates_for_entity() {
    var ownerEmail = generateOwnerEmail();
    var projectId = create(ownerEmail);

    var updateCmdBuilder = newUpdateCommand(projectId, "my name", ownerEmail, ownerEmail);
    self.update(updateCmdBuilder);

    var newOwnerEmail = generateOwnerEmail();
    var updateCmdBuilder2 = newUpdateCommand(projectId, "my new name", ownerEmail, newOwnerEmail);

    assertThatCode(() -> self.update(updateCmdBuilder2))
        .isInstanceOfSatisfying(StatusRuntimeException.class,
            ex -> assertThat(ex.getStatus().getCode()).isEqualTo(Status.FAILED_PRECONDITION.getCode()));

    var actual = listOfProjects(ownerEmail, mapProjectToName);
    assertThat(actual).containsExactly("my name");
  }

  @Test
  @SneakyThrows
  void should_initialize_predefined_projects() {
    val knownOwnerOfPredefinedProjects = "siudeks@gmail.com";
    var reply = self.list(ListRequest.newBuilder().setEmailOfRequestor(knownOwnerOfPredefinedProjects).build());
    var actual = reply.getProjectsList();
    assertThat(actual).size().isNotZero();

  }


  @Test
  public void getHealth() {
      given()
              .when().get("/q/health")
              .then()
              .statusCode(200)
              .body("status", Matchers.equalTo("UP"));
  }

  @Test
  public void getDatasourceHealth() {
      given()
              .when().get("/q/health/ready")
              .then()
              .statusCode(200)
              .body("status", Matchers.equalTo("UP"));
  }

  private String generateOwnerEmail() {
    // %tT - time format hhmmss    
    var randomPart = RandomStringUtils.randomAlphabetic(6);
    return String.format("%tT-%s@example.com", LocalDateTime.now(), randomPart);
  }

  private ProjectId create(String emailOfUser) {
    var reserveCmd = CreateRequest.newBuilder()
        .setUserToken(UserToken.newBuilder()
            .setRequestorEmail(emailOfUser))
        .build();
    var reserveResult = self.create(reserveCmd);
    return reserveResult.getEntityId();
  }

  private static UpdateCommand newUpdateCommand(ProjectId projectId, String name, String newOwner, String currentOwner) {
    return UpdateCommand.newBuilder()
        .setEntityId(projectId)
        .setModel(ProjectModel.newBuilder()
            .setName(name)
            .setEmailOfOwner(newOwner))
        .setUserToken(UserToken.newBuilder()
            .setRequestorEmail(currentOwner))
        .build();
  }

  private static RemoveCommand newRemoveCommand(ProjectId projectId, String emailOfOwner) {
    return RemoveCommand.newBuilder()
        .setProjectId(projectId)
        .setUserToken(UserToken.newBuilder()
            .setRequestorEmail(emailOfOwner)
            .build())
        .build();
  }

  private ProjectId update(ProjectId projectId, String name, String emailOfOwner) {
    return this.update(projectId, name, emailOfOwner, emailOfOwner);
  }

  private ProjectId update(ProjectId projectId, String name, String emailOfOwner, String newOwner) {
    var updateCmd = newUpdateCommand(projectId, name, emailOfOwner, newOwner);
    var result = self.update(updateCmd);
    return result.getEntityId();
  }

  private boolean remove(ProjectId projectId, String emailOfOwner) {
    var cmd = newRemoveCommand(projectId, emailOfOwner);
    var result = self.remove(cmd);
    return result.getSuccess();
  }

  private <T> List<T> listOfProjects(String emailOfOwner, Function<Project, T> map) {
    var listQuery = ListRequest.newBuilder()
        .setEmailOfRequestor(emailOfOwner)
        .build();
    var listResponse = self.list(listQuery);
    return listResponse.getProjectsList().stream().map(map).collect(Collectors.toList());
  }

}
