package sinnet;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.vavr.Function1;
import lombok.SneakyThrows;
import lombok.val;
import sinnet.grpc.projects.Project;
import sinnet.grpc.projects.ProjectModel;

@SpringBootTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ProjectsITest {

  @Autowired
  AppOperations operations;

  private static Function1<Project, String> mapProjectToName = Function1.of(Project::getModel)
      .andThen(ProjectModel::getName);

  
  
  @Test
  void should_create() {
    var ownerName = operations.generateOwnerEmail();
    var projectId = operations.create(ownerName);

    var updatedId = operations.update(projectId, "my name", ownerName);
    assertThat(updatedId.getEId()).isEqualTo(projectId.getEId());
    assertThat(updatedId.getETag()).isEqualTo(projectId.getETag() + 1);

    var projectsOfOwner = operations.listOfProjects(ownerName, Function1.identity());
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
    var ownerName = operations.generateOwnerEmail();

    // lets create 10 free projects
    val limit_of_free_projects = 10;
    for (int i = 0; i < limit_of_free_projects; i++) {
      operations.update(operations.create(ownerName), "my name", ownerName);
    }


    Assertions
        .assertThatCode(() -> operations.update(operations.create(ownerName), "my name", ownerName))
        .isInstanceOfSatisfying(StatusRuntimeException.class,
            ex -> assertThat(ex.getStatus().getCode()).isEqualTo(Status.RESOURCE_EXHAUSTED.getCode()));
  }

  @Test
  void should_remove() {
    var ownerName = operations.generateOwnerEmail();
    var projectId = operations.create(ownerName);

    operations.update(projectId, "my name", ownerName);
    operations.remove(projectId, ownerName);

    var projectsOfOwner = operations.listOfProjects(ownerName, Function1.identity());
    assertThat(projectsOfOwner)
        .as("List of projects does not contain just deleted project")
        .isEmpty();
  }

  @Test
  public void should_update() {
    var ownerEmail = operations.generateOwnerEmail();
    
    var projectId = operations.create(ownerEmail);

    var updateCmd = operations.newUpdateCommand(projectId, "my name", ownerEmail, ownerEmail);
    var updateResult = operations.update(updateCmd);

    var newOwnerEmail = operations.generateOwnerEmail();
    updateCmd = operations.newUpdateCommand(updateResult.getEntityId(), "my new name", newOwnerEmail, ownerEmail);
    operations.update(updateCmd);

    var projectsOfOwner = operations.listOfProjects(ownerEmail, mapProjectToName);
    assertThat(projectsOfOwner).isEmpty();

    var projectsOfNewOwner = operations.listOfProjects(newOwnerEmail, mapProjectToName);
    assertThat(projectsOfNewOwner).containsExactly("my new name");
  }

  @Test
  void should_reject_stale_updates_for_entity() {
    var ownerEmail = operations.generateOwnerEmail();
    var projectId = operations.create(ownerEmail);

    var updateCmdBuilder = operations.newUpdateCommand(projectId, "my name", ownerEmail, ownerEmail);
    operations.update(updateCmdBuilder);

    var newOwnerEmail = operations.generateOwnerEmail();
    var updateCmdBuilder2 = operations.newUpdateCommand(projectId, "my new name", ownerEmail, newOwnerEmail);

    assertThatCode(() -> operations.update(updateCmdBuilder2))
        .isInstanceOfSatisfying(StatusRuntimeException.class,
            ex -> assertThat(ex.getStatus().getCode()).isEqualTo(Status.FAILED_PRECONDITION.getCode()));

    var actual = operations.listOfProjects(ownerEmail, mapProjectToName);
    assertThat(actual).containsExactly("my name");
  }

  @Test
  @SneakyThrows
  void should_initialize_predefined_projects() {
    val knownOwnerOfPredefinedProjects = "siudeks@gmail.com";
    var actual = operations.listOfProjects(knownOwnerOfPredefinedProjects, Function1.identity());
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

}
