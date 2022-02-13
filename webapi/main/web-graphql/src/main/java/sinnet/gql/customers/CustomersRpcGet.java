package sinnet.gql.customers;

import java.util.UUID;

import org.springframework.stereotype.Component;

import io.grpc.stub.StreamObserver;
import io.vertx.core.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import sinnet.bus.AskTemplate;
import sinnet.bus.query.FindCustomer;
import sinnet.gql.Handlers;
import sinnet.grpc.customers.GetReply;
import sinnet.grpc.customers.GetRequest;

@Component
@Slf4j
public class CustomersRpcGet extends AskTemplate<FindCustomer.Ask, FindCustomer.Reply>
                             implements Mapper {

  public CustomersRpcGet(EventBus eventBus) {
    super(FindCustomer.Ask.ADDRESS, FindCustomer.Reply.class, eventBus);
  }

  void query(GetRequest request, StreamObserver<GetReply> responseObserver) {
    var projectId = UUID.fromString(request.getEntityId().getProjectId());
    var entityId = UUID.fromString(request.getEntityId().getEntityId());
    var query = new FindCustomer.Ask(projectId, entityId);
    super.ask(query)
        .onComplete(Handlers.logged(log, responseObserver, it -> {
          var result = toDto(projectId, it);
          return GetReply.newBuilder().setModel(result).build();
        }));
  }

}
