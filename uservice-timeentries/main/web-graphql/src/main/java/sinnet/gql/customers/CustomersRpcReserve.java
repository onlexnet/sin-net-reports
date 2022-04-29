package sinnet.gql.customers;

import java.util.UUID;

import org.springframework.stereotype.Component;

import io.grpc.stub.StreamObserver;
import sinnet.grpc.customers.ReserveReply;
import sinnet.grpc.customers.ReserveRequest;
import sinnet.models.EntityId;

@Component
public class CustomersRpcReserve implements sinnet.gql.common.Mapper {

  void command(ReserveRequest request, StreamObserver<ReserveReply> responseObserver) {
    var projectId = UUID.fromString(request.getProjectId());
    var entity = EntityId.anyNew(projectId);
    var result = ReserveReply.newBuilder()
        .setEntityId(toDto(entity))
        .build();
    responseObserver.onNext(result);
    responseObserver.onCompleted();
  }
}

