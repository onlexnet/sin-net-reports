package sinnet.grpc.users;

import org.springframework.stereotype.Component;

import sinnet.grpc.mapping.RpcCommandHandler;

/**
 * TBD.
 */
@Component
class UsersRpcIncludeOperator
             implements RpcCommandHandler<IncludeOperatorCommand, IncludeOperatorResult> {

  @Override
  public IncludeOperatorResult apply(IncludeOperatorCommand cmd) {
    return IncludeOperatorResult.newBuilder()
        .setSuccess(true).build();
  }
  
}
