package sinnet;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import io.grpc.ManagedChannelBuilder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import sinnet.grpc.projects.CreateRequest;
import sinnet.grpc.projects.GetRequest;
import sinnet.grpc.projects.ListRequest;
import sinnet.grpc.projects.Project;
import sinnet.grpc.projects.ProjectId;
import sinnet.grpc.projects.ProjectModel;
import sinnet.grpc.projects.ProjectsGrpc;
import sinnet.grpc.projects.RemoveCommand;
import sinnet.grpc.projects.UpdateCommand;
import sinnet.grpc.projects.UpdateResult;
import sinnet.grpc.projects.UserToken;

@Component
@RequiredArgsConstructor
public class AppOperations implements ApplicationListener<ApplicationReadyEvent> {

  private ProjectsGrpc.ProjectsBlockingStub self;
  private final TestRestTemplate restTemplate;

  ProjectId create(String emailOfUser) {
    var reserveCmd = CreateRequest.newBuilder()
        .setUserToken(UserToken.newBuilder()
            .setRequestorEmail(emailOfUser))
        .build();
    var reserveResult = self.create(reserveCmd);
    return reserveResult.getEntityId();
  }

  String generateUserEmail() {
    return generateOwnerEmail();
  }

  @Deprecated
  String generateOwnerEmail() {
    // %tT - time format hhmmss    
    var randomPart = RandomStringUtils.randomAlphabetic(6);
    return String.format("%tT-%s@example.com", LocalDateTime.now(), randomPart);
  }

  UpdateResult update(UpdateCommand cmd) {
    return self.update(cmd);
  }

  ProjectId update(ProjectId projectId, String name, String emailOfOwner) {
    return this.update(projectId, name, emailOfOwner, emailOfOwner);
  }

  ProjectId update(ProjectId projectId, String name, String emailOfOwner, String newOwner) {
    var updateCmd = newUpdateCommand(projectId, name, emailOfOwner, newOwner);
    var result = self.update(updateCmd);
    return result.getEntityId();
  }

  ProjectId update(String currentOwner, ProjectId projectId, ProjectModel projectModel) {
    var cmd = UpdateCommand.newBuilder()
        .setEntityId(projectId)
        .setDesired(projectModel)
        .setUserToken(UserToken.newBuilder()
            .setRequestorEmail(currentOwner))
        .build();
    var result = self.update(cmd);
    return result.getEntityId();
  }

  boolean remove(ProjectId projectId, String emailOfOwner) {
    var cmd = newRemoveCommand(projectId, emailOfOwner);
    var result = self.remove(cmd);
    return result.getSuccess();
  }

  ProjectModel get(ProjectId id) {
    var request = GetRequest.newBuilder()
        .setProjectId(id)
        .build();
    var getReply = self.get(request);
    return getReply.getModel();
  }

  static UpdateCommand newUpdateCommand(ProjectId projectId, String projectName, String newOwner, String currentOwner, String... operators) {
    return UpdateCommand.newBuilder()
        .setEntityId(projectId)
        .setDesired(ProjectModel.newBuilder()
            .setName(projectName)
            .setEmailOfOwner(newOwner)
            .addAllEmailOfOperator(Arrays.asList(operators)))
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

  <T> List<T> listOfProjects(String emailOfOwner, Function<Project, T> map) {
    var listQuery = ListRequest.newBuilder()
        .setEmailOfRequestor(emailOfOwner)
        .build();
    var listResponse = self.list(listQuery);
    return listResponse.getProjectsList().stream().map(map).collect(Collectors.toList());
  }

  @Data
  static public class GrpcActuatorModel {
     private Integer port;
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    var grpcModel = restTemplate.getForObject("/actuator/grpc", GrpcActuatorModel.class);
    var grpcPort = grpcModel.getPort();

    var channel = ManagedChannelBuilder.forAddress("localhost", grpcPort)
        .usePlaintext()
        .build();
    self = ProjectsGrpc.newBlockingStub(channel);
  }
}
