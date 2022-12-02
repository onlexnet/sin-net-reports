package sinnet.features;

import static sinnet.grpc.timeentries.ReserveCommand.newBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.google.protobuf.ByteString;

import io.dapr.v1.DaprAppCallbackProtos.TopicEventRequest;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import sinnet.grpc.common.UserToken;
import sinnet.grpc.timeentries.LocalDate;
import sinnet.grpc.users.IncludeOperatorCommand;
import sinnet.models.ProjectId;
import sinnet.models.ValEmail;
import sinnet.models.ValName;
import sinnet.project.events.ProjectCreatedEvent;
import sinnet.user.AvroObjectSerializer;

@RequiredArgsConstructor
@Component
public class TestApi {

  private final RpcApi rpcApi;

  private HashMap<ValEmail, ClientContext> sessions = new HashMap<>();
  private final AvroObjectSerializer objectSerializer = new AvroObjectSerializer();

  @SneakyThrows
  void notifyNewProject(ClientContext ctx, ValName projectAlias) {
    var projectId = ctx.setProjectId(projectAlias);
    var event = ProjectCreatedEvent.newBuilder()
        .setEid(projectId.getId().toString())
        .setEtag(projectId.getVersion())
        .build();
    var eventSerialized = objectSerializer.serialize(event);
    var data = ByteString.copyFrom(eventSerialized);
    var te = TopicEventRequest.newBuilder().setData(data).build();
    rpcApi.getApiCallback().onTopicEvent(te);
  }

  void assignOperator(ClientContext ctx) {
    var projectAlias = ctx.getCurrentProject();
    var operatorAlias = ctx.getCurrentOperator();
    var projectId = ctx.getProjectId(projectAlias);
    var operatorId = ctx.getOperatorId(operatorAlias, false);
    var cmd = IncludeOperatorCommand.newBuilder()
        .setProjectId(projectId.getId().toString())
        .addOperatorEmail(operatorId.getValue())
        .build();
    rpcApi.getUsers().includeOperator(cmd);
  }

  void createEntry(ClientContext ctx) {
    var projectAlias = ctx.getCurrentProject();
    var operatorAlias = ctx.getCurrentOperator();
    var projectId = ctx.getProjectId(projectAlias);
    var operatorId = ctx.getOperatorId(operatorAlias, false);
    var invoker = UserToken.newBuilder()
        .setProjectId(projectId.getId().toString())
        .setRequestorEmail(operatorId.getValue());
    rpcApi.getTimeentries().reserve(newBuilder()
        .setInvoker(invoker)
        .build());
  }
}

class ClientContext {
  @Getter
  private ValName currentOperator;
  @Getter
  private ValName currentProject;

  private final Map<ValName, ValEmail> knownUsers = new HashMap<>();
  private final Map<ValName, ProjectId> knownProjects = new HashMap<>();

  public ProjectId setProjectId(@NonNull ValName projectAlias) {
    currentProject = projectAlias;
    var projectId = ProjectId.anyNew();
    knownProjects.put(projectAlias, projectId);
    return knownProjects.get(projectAlias);
  }
  public ProjectId getProjectId(@NonNull ValName projectAlias) {
    return knownProjects.get(projectAlias);
  }

  public ValEmail getOperatorId(@NonNull ValName operatorAlias, boolean setCurrent) {
    if (setCurrent)
      currentOperator = operatorAlias;
    var actual = knownUsers.get(operatorAlias);
    if (actual != null)
      return actual;
    var emailAsString = "user@" + UUID.randomUUID();
    var email = ValEmail.of(emailAsString);
    knownUsers.put(operatorAlias, email);
    return email;
  }
}
