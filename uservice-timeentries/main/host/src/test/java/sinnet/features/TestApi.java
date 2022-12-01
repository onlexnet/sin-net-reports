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
  void notifyNewProject(ClientContext ctx, String projectAlias) {
    var projectId = UUID.randomUUID();
    var event = ProjectCreatedEvent.newBuilder()
        .setEid(projectId.toString())
        .setEtag(1)
        .build();
    var eventSerialized = objectSerializer.serialize(event);
    var data = ByteString.copyFrom(eventSerialized);
    var te = TopicEventRequest.newBuilder().setData(data).build();
    rpcApi.getApiCallback().onTopicEvent(te);
    ctx.getKnownProjects().put(ValName.of(projectAlias), projectId);
  }

}

@Data
class ClientContext {
  private Map<ValName, UUID> knownProjects = new HashMap<>();
}
