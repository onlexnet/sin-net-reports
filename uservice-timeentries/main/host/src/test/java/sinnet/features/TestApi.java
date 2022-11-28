package sinnet.features;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.google.protobuf.ByteString;

import io.dapr.v1.DaprAppCallbackProtos.TopicEventRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import sinnet.models.ValEmail;
import sinnet.project.events.ProjectCreatedEvent;
import sinnet.user.AvroObjectSerializer;

@RequiredArgsConstructor
@Component
public class TestApi {

  private final RpcApi rpcApi;

  private HashMap<ValEmail, ClientContext> sessions = new HashMap<>();
  private final AvroObjectSerializer objectSerializer = new AvroObjectSerializer();

  @SneakyThrows
  void notifyNewProject(ClientContext ctx) {
    var projectId = UUID.randomUUID();
    var event = ProjectCreatedEvent.newBuilder()
        .setEid(projectId.toString())
        .setEtag(1)
        .build();
    var a = objectSerializer.serialize(event);
    var data = ByteString.copyFrom(a);
    var te = TopicEventRequest.newBuilder().setData(data).build();
    rpcApi.getApiCallback().onTopicEvent(te);
  }

}

@Data
class ClientContext {
  private List<UUID> knownProjects = new LinkedList<>();
}
