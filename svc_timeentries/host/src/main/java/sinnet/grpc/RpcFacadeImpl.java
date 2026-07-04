package sinnet.grpc;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import sinnet.grpc.infra.GrpcServer;

@Component
@RequiredArgsConstructor
class RpcFacadeImpl implements RpcFacade {

  @Delegate(types = RpcFacade.class)
  private final GrpcServer grpcServer;
  
}
