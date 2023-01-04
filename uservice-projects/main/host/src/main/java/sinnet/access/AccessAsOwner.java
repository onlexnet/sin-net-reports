package sinnet.access;

import org.springframework.stereotype.Component;

import io.vavr.collection.Array;
import sinnet.grpc.projects.UserToken;

@Component
final class AccessAsOperator implements AccessAs<RoleContextSet.OperatorRoleContext> {

  @Override
  public RoleContextSet.OperatorRoleContext calculate(UserToken userToken) {
    return new RoleContextSet.OperatorRoleContext(Array.empty());
  }
}
