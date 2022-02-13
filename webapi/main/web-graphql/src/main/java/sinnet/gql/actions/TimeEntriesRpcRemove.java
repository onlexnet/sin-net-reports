package sinnet.gql.actions;

import org.springframework.stereotype.Component;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sinnet.ActionRepository;
import sinnet.gql.Handlers;
import sinnet.grpc.timeentries.RemoveCommand;
import sinnet.grpc.timeentries.RemoveResult;

@Component
@RequiredArgsConstructor
@Slf4j
public class TimeEntriesRpcRemove implements sinnet.gql.common.Mapper {

    private final ActionRepository actionService;

    public void remove(RemoveCommand request, StreamObserver<RemoveResult> responseObserver) {
        var id = fromDto(request.getEntityId());
        actionService
            .remove(id)
            .onComplete(Handlers.logged(log, responseObserver, it ->
                RemoveResult.newBuilder()
                    .setResult(it)
                    .build()));
    }

}
