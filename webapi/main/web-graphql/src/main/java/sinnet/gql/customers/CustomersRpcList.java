package sinnet.gql.customers;

import static sinnet.grpc.PropsBuilder.ofNullable;

import java.util.UUID;

import org.springframework.stereotype.Component;

import io.grpc.stub.StreamObserver;
import io.vavr.collection.List;
import io.vertx.core.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import sinnet.bus.AskTemplate;
import sinnet.bus.query.FindCustomers;
import sinnet.grpc.PropsBuilder;
import sinnet.grpc.common.EntityId;
import sinnet.grpc.customers.CustomerModel;
import sinnet.grpc.customers.ListReply;
import sinnet.grpc.customers.ListRequest;
import sinnet.vertx.Handlers;

@Component
@Slf4j
class CustomersRpcList extends AskTemplate<FindCustomers.Ask, FindCustomers.Reply>
                       implements sinnet.gql.common.Mapper, Mapper {

  CustomersRpcList(EventBus eventBus) {
    super(FindCustomers.Ask.ADDRESS, FindCustomers.Reply.class, eventBus);
  }

  void query(ListRequest request, StreamObserver<ListReply> stream) {
    var projectId = UUID.fromString(request.getProjectId());
    var userToken = fromDto(request.getUserToken());
    var ask = new FindCustomers.Ask(projectId, userToken);
    super.ask(ask)
      .onComplete(Handlers.logged(log, stream, it -> {
        var result = List.of(it.getData()).map(o -> toDto(o));
        return ListReply.newBuilder().addAllCustomers(result).build();
      }));
  }

  static CustomerModel map(FindCustomers.CustomerData it) {
    return PropsBuilder.build(CustomerModel.newBuilder())
      .tset(ofNullable(it, CustomersRpcList::mapAsId), b -> b::setId)
      .done().build();
  }

  static EntityId mapAsId(FindCustomers.CustomerData it) {
    return PropsBuilder.build(EntityId.newBuilder())
      .tset(ofNullable(it.getProjectId(), UUID::toString) , b -> b::setProjectId)
      .tset(ofNullable(it.getEntityId(), UUID::toString), b -> b::setEntityId)
      .tset(ofNullable(it.getEntityVersion()), b -> b::setEntityVersion)
      .done().build();
  }
}
