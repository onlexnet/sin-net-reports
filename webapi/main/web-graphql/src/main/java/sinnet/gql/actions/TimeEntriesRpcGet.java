package sinnet.gql.actions;

import java.util.UUID;

import org.springframework.stereotype.Component;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sinnet.grpc.timeentries.GetQuery;
import sinnet.grpc.timeentries.GetReply;
import sinnet.read.ActionProjector;
import sinnet.vertx.Handlers;
import sinnet.write.ActionRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class TimeEntriesRpcGet implements Mapper {

  private final ActionProjector.Provider actionRepository;

  public void query(GetQuery query, StreamObserver<GetReply> responseObserver) {
    var projectId = UUID.fromString(query.getProjectId());
    var itemId = UUID.fromString(query.getTimeentryId());
    actionRepository
      .find(projectId, itemId)
      .onComplete(Handlers.logged(log, responseObserver, it -> {
        var response = toDto(it);
        return GetReply.newBuilder()
            .setItem(response)
            .build();
      }));
  }
}
