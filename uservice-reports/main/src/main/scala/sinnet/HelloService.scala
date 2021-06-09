package sinnet

import io.grpc.stub.StreamObserver;
import io.quarkus.example.GreeterGrpc;
import io.quarkus.example.HelloReply;
import io.quarkus.example.HelloRequest;

import javax.inject.Singleton;
import io.grpc.Context
import java.util.concurrent.TimeUnit

@Singleton
class HelloService extends GreeterGrpc.GreeterImplBase {

    override def sayHello(request: HelloRequest, responseObserver: StreamObserver[HelloReply]) {
        val deadline = Option(Context.current().getDeadline()) match {
            case Some(value) => value.timeRemaining(TimeUnit.MILLISECONDS)
            case None => "undefined"
        }
        val name = request.getName()
        val message = s"Hello from uservice Reposts, $name, deadline for response is: $deadline"
        responseObserver.onNext(HelloReply.newBuilder().setMessage(message).build())
        responseObserver.onCompleted()
    }
}