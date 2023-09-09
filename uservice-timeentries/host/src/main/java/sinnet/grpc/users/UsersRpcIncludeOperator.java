package sinnet.grpc.users;

import org.springframework.stereotype.Component;

import sinnet.grpc.mapping.RpcCommandHandlerBase;

/**
 * TBD.
 */
@Component
class UsersRpcIncludeOperator extends RpcCommandHandlerBase<IncludeOperatorCommand, IncludeOperatorResult> {

  @Override
  protected IncludeOperatorResult apply(IncludeOperatorCommand cmd) {
    return IncludeOperatorResult.newBuilder()
        .setSuccess(true).build();
  }
  
}
