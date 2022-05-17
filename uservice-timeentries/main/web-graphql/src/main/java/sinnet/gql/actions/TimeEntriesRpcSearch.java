package sinnet.gql.actions;

import java.util.UUID;

import org.springframework.stereotype.Component;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sinnet.grpc.PropsBuilder;
import sinnet.grpc.timeentries.SearchQuery;
import sinnet.grpc.timeentries.SearchReply;
import sinnet.grpc.timeentries.TimeEntryModel;
import sinnet.models.ActionValue;
import sinnet.read.ActionProjector;
import sinnet.vertx.Handlers;

@Component
@RequiredArgsConstructor
@Slf4j
public class TimeEntriesRpcSearch implements ActionProjector, Mapper {

  private final ActionProjector.Provider projection;

  public void query(SearchQuery query, StreamObserver<SearchReply> observer) {
    var projectId = UUID.fromString(query.getProjectId());
    var from = fromDto(query.getFrom());
    var to = fromDto(query.getTo());
    projection
        .find(projectId, from, to)
        .onComplete(Handlers.logged(log, observer, it -> {
            var result = it.map(this::map);
            return SearchReply.newBuilder()
                .addAllActivities(result)
                .build();
        }));
  }

    private TimeEntryModel map(ListItem item) {
        return PropsBuilder.build(TimeEntryModel.newBuilder())
            .set(item.getEid(), this::toDto, b -> b::setEntityId)
            .set(item.getValue(), ActionValue::getWhom, o -> o.toString(), b -> b::setCustomerId)
            .set(item.getValue(), o -> o.getWho().getValue(), b -> b::setServicemanEmail)
            .set(item.getServicemanName(), b -> b::setServicemanName)
            .set(item.getValue(), o -> o.getWhen(), o -> toDto(o), b -> b::setWhenProvided)
            .set(item.getValue(), o -> o.getWhat(), b -> b::setDescription)
            .set(item.getValue(), o -> o.getHowLong().getValue(), b -> b::setDuration)
            .set(item.getValue(), o -> o.getHowFar().getValue(), b -> b::setDistance)
            .done().build();
    }

}
