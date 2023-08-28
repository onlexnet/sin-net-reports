package sinnet.grpc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sinnet.gql.api.CommonMapper;
import sinnet.grpc.timeentries.SearchQuery;
import sinnet.grpc.timeentries.TimeEntriesGrpc.TimeEntriesBlockingStub;
import sinnet.grpc.timeentries.TimeEntryModel;


/** Mockable equivalent of {@link ProjectsGrpcStub}. */
@Component
@RequiredArgsConstructor
class ActionsGrpcFacadeImpl implements ActionsGrpcFacade {

  private final TimeEntriesBlockingStub stub;

  @Override
  public <T> List<T> search(UUID projectId, LocalDate from, LocalDate to, Function<TimeEntryModel, T> mapper) {

    var searchQuery = SearchQuery.newBuilder()
        .setFrom(CommonMapper.toGrpc(from))
        .setTo(CommonMapper.toGrpc(to))
        .setProjectId(projectId.toString())
        .build();

    var result = stub.search(searchQuery);

    return result.getActivitiesList().stream().map(mapper).toList();
  }

  // @Override
  // public ProjectId create(String requestorEmail) {
  //   var requestorToken = UserToken.newBuilder()
  //       .setRequestorEmail(requestorEmail)
  //       .build();
  //   var createRequest = CreateRequest.newBuilder()
  //       .setUserToken(requestorToken)
  //       .build();

  //   var createResult = stub.create(createRequest);
  //   var id = createResult.getEntityId();

  //   return new ProjectId(id.getEId(), id.getETag());
  // }

  // @Override
  // public ProjectId update(String requestorEmail, ProjectId id, String name, String ownerEmail, List<String> operatorEmail) {
  //   var requestorToken = UserToken.newBuilder()
  //       .setRequestorEmail(requestorEmail)
  //       .build();
  //   var desired = ProjectModel.newBuilder()
  //       .setName(name)
  //       .setEmailOfOwner(requestorEmail);
  //   var updateRequest = UpdateCommand.newBuilder()
  //       .setUserToken(requestorToken)
  //       .setEntityId(sinnet.grpc.projects.generated.ProjectId.newBuilder()
  //         .setEId(id.id())
  //         .setETag(id.tag()))
  //       .setDesired(desired)
  //       .build();
  //   var updatedId = stub.update(updateRequest).getEntityId();
  //   return new ProjectId(updatedId.getEId(), updatedId.getETag());
  // }

  // @Override
  // public List<ProjectEntityGql> list(String requestorEmail, Function1<Project, ProjectEntityGql> mapper) {
  //   var request = ListRequest.newBuilder()
  //       .setEmailOfRequestor(requestorEmail)
  //       .build();
  //   return stub.list(request)
  //       .getProjectsList()
  //       .stream()
  //       .map(mapper::apply)
  //       .toList();
  // }

  // @Override
  // public StatsResult userStats(String requestorEmail) {
  //   var request = UserStatsRequest.newBuilder()
  //       .setEmailOfRequestor(requestorEmail)
  //       .build();
  //   var dto = stub.userStats(request);
  //   return new StatsResult(dto.getNumberOfProjects());
  // }

}
