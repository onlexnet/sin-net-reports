package sinnet.features;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.google.protobuf.ByteString;

import io.dapr.v1.DaprAppCallbackProtos.TopicEventRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
    var projectId = ProjectId.anyNew();
    var event = ProjectCreatedEvent.newBuilder()
        .setEid(projectId.getId().toString())
        .setEtag(projectId.getVersion())
        .build();
    var eventSerialized = objectSerializer.serialize(event);
    var data = ByteString.copyFrom(eventSerialized);
    var te = TopicEventRequest.newBuilder().setData(data).build();
    rpcApi.getApiCallback().onTopicEvent(te);
    ctx.getKnownProjects().put(projectAlias, projectId);
  }

  void assignOperator(ClientContext ctx, ValName projectAlias, ValName operatorAlias) {
    var projectId = ctx.getKnownProjects().get(projectAlias);
    var cmd = IncludeOperatorCommand.newBuilder()
        .setProjectId(projectId.getId().toString())
        .addOperatorEmail(operatorAlias.getValue())
        .build();
    rpcApi.getUsers().includeOperator(cmd);
  }

}

@Data
class ClientContext {
  private Map<ValName, ProjectId> knownProjects = new HashMap<>();
}
