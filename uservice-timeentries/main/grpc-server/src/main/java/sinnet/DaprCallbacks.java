package sinnet;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.protobuf.Empty;

import io.dapr.v1.AppCallbackGrpc;
import io.dapr.v1.DaprAppCallbackProtos.ListTopicSubscriptionsResponse;
import io.dapr.v1.DaprAppCallbackProtos.TopicEventRequest;
import io.dapr.v1.DaprAppCallbackProtos.TopicEventResponse;
import io.dapr.v1.DaprAppCallbackProtos.TopicEventResponse.TopicEventResponseStatus;
import io.dapr.v1.DaprAppCallbackProtos.TopicSubscription;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class DaprCallbacks extends AppCallbackGrpc.AppCallbackImplBase {

    private final List<TopicHandler> handlers;

    @Override
    public void onTopicEvent(TopicEventRequest request, StreamObserver<TopicEventResponse> responseObserver) {
        var topicName = request.getTopic();
        var data = request.getData();
        for (var topicHandler: handlers) {
            if (!topicHandler.canHandle(topicName)) continue;
            topicHandler.handle(data);
        }

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
