package sinnet

import io.grpc.stub.StreamObserver;
import io.quarkus.example.GreeterGrpc;
import io.quarkus.example.HelloReply;
import io.quarkus.example.HelloRequest;

import javax.inject.Singleton;

@Singleton                                                                                    
class HelloService extends GreeterGrpc.GreeterImplBase {                               

    override def sayHello(request: HelloRequest, responseObserver: StreamObserver[HelloReply]) { 
        val name = request.getName;
        val message = "Hello from uservice Reposts, " + name;
        responseObserver.onNext(HelloReply.newBuilder().setMessage(message).build());         
        responseObserver.onCompleted();                                                       
    }
}