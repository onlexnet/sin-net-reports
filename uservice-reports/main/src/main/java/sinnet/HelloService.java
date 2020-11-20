package sinnet;

import javax.inject.Singleton;

import io.grpc.stub.StreamObserver;
import sinnet.gen.HelloRequest;
import sinnet.gen.GreeterGrpc;
import sinnet.gen.HelloReply;

@Singleton
public class HelloService extends GreeterGrpc.GreeterImplBase {

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        var name = request.getName();
        var message = "Hello " + name;
        responseObserver.onNext(HelloReply.newBuilder().setMessage(message).build());
        responseObserver.onCompleted();
    }
}
