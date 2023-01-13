package sinnet.web;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import sinnet.rpc.RpcAdapter;

@Component
@Endpoint(id = "grpc")
@RequiredArgsConstructor
class RpcActuatorEndpoint {

  private final RpcAdapter rpcAdapter;
  
  @ReadOperation
  public MyModel getPort() {
    var port = rpcAdapter.getServerPort().orElse(-1);
    return new MyModel(port);
  }
}

@Data
@AllArgsConstructor
class MyModel {
  final int port;
}
