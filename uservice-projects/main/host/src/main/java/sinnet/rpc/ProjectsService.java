package sinnet.rpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import sinnet.grpc.projects.CreateReply;
import sinnet.grpc.projects.CreateRequest;
import sinnet.grpc.projects.GetReply;
import sinnet.grpc.projects.GetRequest;
import sinnet.grpc.projects.ListReply;
import sinnet.grpc.projects.ListRequest;
import sinnet.grpc.projects.RemoveCommand;
import sinnet.grpc.projects.RemoveResult;
import sinnet.grpc.projects.UpdateCommand;
import sinnet.grpc.projects.UpdateResult;
import sinnet.grpc.projects.UserStatsReply;
import sinnet.grpc.projects.UserStatsRequest;
import sinnet.grpc.projects.ProjectsGrpc.ProjectsImplBase;

@RequiredArgsConstructor
class ProjectsService extends ProjectsImplBase {

  private final ProjectsCreateImpl projectsCreate;
  private final ProjectsUpdateImpl projectsUpdate;
  private final ProjectListImpl projectsList;
  private final ProjectGetImpl projectsGet;
  private final ProjectsRemoveImpl projectsRemove;
  private final ProjectUserStatsImpl projectUserStats;

  @Override
  public void create(CreateRequest request, StreamObserver<CreateReply> responseObserver) {
    projectsCreate.command(request, responseObserver);
  }

  @Override
  public void update(UpdateCommand request, StreamObserver<UpdateResult> responseObserver) {
    projectsUpdate.command(request, responseObserver);
  }

  @Override
  public void list(ListRequest request, StreamObserver<ListReply> responseObserver) {
    projectsList.query(request, responseObserver);
  }  

  @Override
  public void get(GetRequest request, StreamObserver<GetReply> responseObserver) {
    projectsGet.query(request, responseObserver);
  }

  @Override
  public void remove(RemoveCommand request, StreamObserver<RemoveResult> responseObserver) {
    projectsRemove.command(request, responseObserver);
  }

  @Override
  public void userStats(UserStatsRequest request, StreamObserver<UserStatsReply> responseObserver) {
    projectUserStats.query(request, responseObserver);
  }

}
