package sinnet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.test.junit.QuarkusTest;
import io.vavr.Function1;
import sinnet.grpc.projects.ListRequest;
import sinnet.grpc.projects.Project;
import sinnet.grpc.projects.ProjectId;
import sinnet.grpc.projects.ProjectModel;
import sinnet.grpc.projects.ProjectsGrpc;
import sinnet.grpc.projects.RemoveCommand;
import sinnet.grpc.projects.ReserveRequest;
import sinnet.grpc.projects.UpdateCommand;
import sinnet.grpc.projects.UserToken;

@QuarkusTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ProjectsITest {

  @Inject
  @GrpcClient
  ProjectsGrpc.ProjectsBlockingStub self;

  private static Function1<Project, String> mapProjectToName = Function1.of(Project::getModel)
      .andThen(ProjectModel::getName);

  @Test
  void should_create() {
    var projectId = reserve();
    var ownerName = generateOwnerEmail();
  

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
  void should_remove() {
    var projectId = reserve();
    var ownerName = generateOwnerEmail();
  
    update(projectId, "my name", ownerName);
    remove(projectId, ownerName);

    var projectsOfOwner = listOfProjects(ownerName, Function1.identity());
    assertThat(projectsOfOwner)
      .as("List of projects does not contain just deleted project")
      .isEmpty();
  }

  @Test
  public void should_update() {
    var reserveCmd = ReserveRequest.newBuilder().build();
    var reserveResult = self.reserve(reserveCmd);

    var ownerEmail = generateOwnerEmail();
    var updateCmd = newUpdateCommand(reserveResult.getEntityId(), "my name", ownerEmail);
    var updateResult = self.update(updateCmd);

    var newOwnerEmail = generateOwnerEmail();
    updateCmd = newUpdateCommand(updateResult.getEntityId(), "my new name", newOwnerEmail);
    self.update(updateCmd);

    var projectsOfOwner = listOfProjects(ownerEmail, mapProjectToName);
    assertThat(projectsOfOwner).isEmpty();

    var projectsOfNewOwner = listOfProjects(newOwnerEmail, mapProjectToName);
    assertThat(projectsOfNewOwner).containsExactly("my new name");
  }

  @Test
  void should_reject_stale_updates_for_entity() {
    var reserveCmd = ReserveRequest.newBuilder().build();
    var reserveResult = self.reserve(reserveCmd);

    var ownerEmail = generateOwnerEmail();
    var updateCmdBuilder = newUpdateCommand(reserveResult.getEntityId(), "my name", ownerEmail);
    self.update(updateCmdBuilder);

    var newOwnerEmail = generateOwnerEmail();
    var updateCmdBuilder2 = newUpdateCommand(reserveResult.getEntityId(), "my new name", newOwnerEmail);

    assertThatCode(() -> self.update(updateCmdBuilder2))
      .isInstanceOfSatisfying(StatusRuntimeException.class, ex -> assertThat(ex.getStatus().getCode()).isEqualTo(Status.FAILED_PRECONDITION.getCode()));

    var actual = listOfProjects(ownerEmail, mapProjectToName);
    assertThat(actual).containsExactly("my name");
  }

  private String generateOwnerEmail() {
    return String.format("%s@example.com", UUID.randomUUID());
  }

  private ProjectId reserve() {
    var reserveCmd = ReserveRequest.newBuilder().build();
    var reserveResult = self.reserve(reserveCmd);
    return reserveResult.getEntityId();
  }

  private static UpdateCommand newUpdateCommand(ProjectId projectId, String name, String emailOfOwner) {
    return UpdateCommand.newBuilder()
        .setEntityId(projectId)
        .setModel(ProjectModel.newBuilder()
          .setName(name)
          .setEmailOfOwner(emailOfOwner))
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
    var updateCmd = newUpdateCommand(projectId, name, emailOfOwner);
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
