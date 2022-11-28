package sinnet.features;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.dapr.v1.AppCallbackGrpc;
import io.dapr.v1.AppCallbackGrpc.AppCallbackBlockingStub;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import sinnet.grpc.users.UsersGrpc;
import sinnet.grpc.users.UsersGrpc.UsersBlockingStub;

@Component
public class RpcApi {
  
  @Getter
  private UsersBlockingStub users;

  @Getter
  private AppCallbackBlockingStub apiCallback;

  @Value("${grpc.server.port}")
  private int grpcPort;

  @PostConstruct
  void init() {
    var channel = ManagedChannelBuilder.forAddress("localhost", grpcPort)
        .usePlaintext()
        .build();
    users = UsersGrpc.newBlockingStub(channel);
    apiCallback = AppCallbackGrpc.newBlockingStub(channel);
  }
  
}
