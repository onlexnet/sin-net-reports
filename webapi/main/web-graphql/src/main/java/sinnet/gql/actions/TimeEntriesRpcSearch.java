package sinnet.gql.actions;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sinnet.gql.Handlers;
import sinnet.grpc.PropsBuilder;
import sinnet.grpc.timeentries.SearchQuery;
import sinnet.grpc.timeentries.SearchReply;
import sinnet.grpc.timeentries.TimeEntryModel;
import sinnet.read.ActionProjector;

@Component
@RequiredArgsConstructor
@Slf4j
public class TimeEntriesRpcSearch implements ActionProjector, Mapper {

    private final ActionProjector.Provider projection;

    /**
     * FixMe.
     *
     * @param gcontext ignored
     * @param filter fixme.
     * @return fixme
     */
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
            .set(item.getEid(), o -> toDto(o), b -> b::setEntityId)
            .set(item.getValue(), o -> o.getWho().getValue(), b -> b::setServicemanName)
            .set(item.getValue(), o -> o.getWhen(), o -> toDto(o), b -> b::setWhenProvided)
            .set(item.getValue(), o -> o.getWhat(), b -> b::setDescription)
            .set(item.getValue(), o -> o.getHowLong().getValue(), b -> b::setDuration)
            .set(item.getValue(), o -> o.getHowFar().getValue(), b -> b::setDistance)
            .done().build();
    }

}

