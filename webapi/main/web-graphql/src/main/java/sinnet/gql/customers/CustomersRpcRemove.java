package sinnet.gql.customers;

import java.util.UUID;

import org.springframework.stereotype.Component;

import io.grpc.stub.StreamObserver;
import io.vertx.core.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import sinnet.bus.AskTemplate;
import sinnet.bus.commands.RemoveCustomer;
import sinnet.gql.Handlers;
import sinnet.grpc.customers.RemoveReply;
import sinnet.grpc.customers.RemoveRequest;
import sinnet.models.EntityId;

@Component
@Slf4j
public class CustomersRpcRemove extends AskTemplate<RemoveCustomer.Command, RemoveCustomer.Result>
                                    implements RemoveCustomer {

  public CustomersRpcRemove(EventBus eventBus) {
    super(Command.ADDRESS, Result.class, eventBus);
  }

  void command(RemoveRequest request, StreamObserver<RemoveReply> responseObserver) {
    var id = request.getEntityId();
    var projectIdAsUUID = UUID.fromString(id.getProjectId());
    var entityIdAsUUID = UUID.fromString(id.getEntityId());
    var entityId = EntityId.of(projectIdAsUUID, entityIdAsUUID, id.getEntityVersion());
    var cmd = RemoveCustomer.Command.builder().id(entityId).build();
    super.ask(cmd).onComplete(Handlers.logged(log, responseObserver, it -> {
      return RemoveReply.newBuilder()
          .setSuccess(Boolean.valueOf(it.getValue()))
          .build();
    }));
  }
}

