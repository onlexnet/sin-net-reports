package sinnet.infra.adapters.grpc;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sinnet.app.ports.out.ProjectsPortOut;
import sinnet.domain.models.ProjectId;
import sinnet.grpc.projects.generated.CreateRequest;
import sinnet.grpc.projects.generated.ListRequest;
import sinnet.grpc.projects.generated.ProjectModel;
import sinnet.grpc.projects.generated.ProjectsGrpc.ProjectsBlockingStub;
import sinnet.grpc.projects.generated.UpdateCommand;
import sinnet.grpc.projects.generated.UserStatsRequest;
import sinnet.grpc.projects.generated.UserToken;


/** Mockable equivalent of {@link ProjectsGrpcStub}. */
@Component
@RequiredArgsConstructor
class ProjectsGrpcGateway implements ProjectsPortOut {

  private final ProjectsBlockingStub stub;

  @Override
  public ProjectId create(String requestorEmail) {
    var requestorToken = UserToken.newBuilder()
        .setRequestorEmail(requestorEmail)
        .build();
    var createRequest = CreateRequest.newBuilder()
        .setUserToken(requestorToken)
        .build();

    var createResult = stub.create(createRequest);
    var id = createResult.getEntityId();

    var projectId = UUID.fromString(id.getEId());
    var projectTag = id.getETag();
    return new ProjectId(projectId, projectTag);
  }

  @Override
  public ProjectId update(String requestorEmail, ProjectId id, String name, String ownerEmail, List<String> operatorEmail) {
    var requestorToken = UserToken.newBuilder()
        .setRequestorEmail(requestorEmail)
        .build();
    var desired = ProjectModel.newBuilder()
        .setName(name)
        .setEmailOfOwner(requestorEmail);
    var updateRequest = UpdateCommand.newBuilder()
        .setUserToken(requestorToken)
        .setEntityId(sinnet.grpc.projects.generated.ProjectId.newBuilder()
          .setEId(id.id().toString())
          .setETag(id.tag()))
        .setDesired(desired)
        .build();
    var updatedId = stub.update(updateRequest).getEntityId();
    return new ProjectId(UUID.fromString(updatedId.getEId()), updatedId.getETag());
  }

  @Override
  public List<sinnet.domain.models.Project> list(String requestorEmail) {
    var request = ListRequest.newBuilder()
        .setEmailOfRequestor(requestorEmail)
        .build();
    return stub.list(request)
        .getProjectsList()
        .stream()
        .map(ProjectMapper::map)
        .toList();
  }

  @Override
  public StatsResult userStats(String requestorEmail) {
    var request = UserStatsRequest.newBuilder()
        .setEmailOfRequestor(requestorEmail)
        .build();
    var dto = stub.userStats(request);
    return new StatsResult(dto.getNumberOfProjects());
  }

}
