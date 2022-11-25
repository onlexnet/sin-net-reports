package sinnet;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Empty;

import io.dapr.v1.AppCallbackGrpc;
import io.dapr.v1.DaprAppCallbackProtos.ListTopicSubscriptionsResponse;
import io.dapr.v1.DaprAppCallbackProtos.TopicEventRequest;
import io.dapr.v1.DaprAppCallbackProtos.TopicEventResponse;
import io.dapr.v1.DaprAppCallbackProtos.TopicEventResponse.TopicEventResponseStatus;
import io.dapr.v1.DaprAppCallbackProtos.TopicSubscription;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import sinnet.events.ProjectCreated;
import sinnet.project.events.ProjectCreatedEvent;

/**
 * Single place to cooperate programatically with DAPR functionality offered for its clients
 * like subscriptions, consuming inputs.
 */
@Component
@RequiredArgsConstructor
class DaprCallbacks extends AppCallbackGrpc.AppCallbackImplBase {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public void onTopicEvent(TopicEventRequest request, StreamObserver<TopicEventResponse> responseObserver) {
        var topicName = request.getTopic();
        var data = request.getData().toStringUtf8();

        var event = objectMapper.readValue(data, ProjectCreatedEvent.class);
        var eidAsString = event.getEid().toString();
        var eid = UUID.fromString(eidAsString);
        var etag = event.getEtag();
        var name = "Undefined";
        var domainAppEvent = ProjectCreated.of(eid, etag, name);
        applicationEventPublisher.publishEvent(domainAppEvent);

        var ack = TopicEventResponse.newBuilder()
            // .setStatus(TopicEventResponseStatus.SUCCESS) - does not work, so we use just numeric status value
            .setStatusValue(TopicEventResponseStatus.SUCCESS.getNumber())
            .build();
        responseObserver.onNext(ack);
        responseObserver.onCompleted();
    }

    @Override
    public void listTopicSubscriptions(Empty request, StreamObserver<ListTopicSubscriptionsResponse> responseObserver) {
        var response = ListTopicSubscriptionsResponse.newBuilder()
            .addSubscriptions(TopicSubscription.newBuilder()
                .setDeadLetterTopic("MYTOPICNAME-D")
                .setPubsubName("MYTOPICNAME"));
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }
}
