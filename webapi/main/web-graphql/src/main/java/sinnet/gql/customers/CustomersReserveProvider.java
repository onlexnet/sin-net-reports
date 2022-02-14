package sinnet.gql.customers;

import java.util.UUID;

import org.springframework.stereotype.Component;

import io.grpc.stub.StreamObserver;
import sinnet.grpc.common.EntityId;
import sinnet.grpc.customers.ReserveReply;
import sinnet.grpc.customers.ReserveRequest;

@Component
public class CustomersReserveProvider {

    void query(ReserveRequest request, StreamObserver<ReserveReply> observer) {
        var projectId = UUID.fromString(request.getProjectId());
        var result = sinnet.models.EntityId.anyNew(projectId);
        var reply = ReserveReply.newBuilder()
            .setEntityId(EntityId.newBuilder()
                .setProjectId(result.getProjectId().toString())
                .setEntityId(result.getId().toString())
                .setEntityVersion(result.getVersion())
                .build())
            .build();
        observer.onNext(reply);
        observer.onCompleted();
    }
}
