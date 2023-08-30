package sinnet.grpc.customers;

import java.util.UUID;

import org.springframework.stereotype.Component;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import sinnet.models.ShardedId;

/**
 * TBD.
 */
@Component
@RequiredArgsConstructor
public class CustomersRpcReserve implements sinnet.grpc.common.Mapper {

  void command(ReserveRequest request, StreamObserver<ReserveReply> responseObserver) {
    var projectId = UUID.fromString(request.getProjectId());
    var entity = ShardedId.anyNew(projectId);

    var result = ReserveReply.newBuilder()
        .setEntityId(toDto(entity))
        .build();
    responseObserver.onNext(result);
    responseObserver.onCompleted();
  }
}

