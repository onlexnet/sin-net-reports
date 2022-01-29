package sinnet;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import io.grpc.examples.helloworld.PingGrpc;
import io.grpc.examples.helloworld.PingReply;
import io.grpc.examples.helloworld.PingRequest;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

@Component
public class MyService extends PingGrpc.PingImplBase {

    @Override
    public void test(PingRequest request, StreamObserver<PingReply> responseObserver) {
        var reply = PingReply
            .newBuilder()
            .setMessage(true)
            .build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
