package sinnet.grpc.customers;

import java.util.UUID;

import org.springframework.stereotype.Component;

import io.grpc.stub.StreamObserver;
import sinnet.grpc.common.EntityId;
import sinnet.models.EntityVersion;

/**
 * TBD.
 */
@Component
public class CustomersReserveProvider {

  void query(ReserveRequest request, StreamObserver<ReserveReply> observer) {
    var projectId = UUID.fromString(request.getProjectId());
    var result = sinnet.models.ShardedId.reserved(projectId);
    var reply = ReserveReply.newBuilder()
        .setEntityId(EntityId.newBuilder()
            .setProjectId(result.projectId().toString())
            .setEntityId(result.id().toString())
            .setEntityVersion(EntityVersion.toDto(result.version()))
            .build())
        .build();
    observer.onNext(reply);
    observer.onCompleted();
  }
}

