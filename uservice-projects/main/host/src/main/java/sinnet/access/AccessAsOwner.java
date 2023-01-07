package sinnet.access;

import org.springframework.stereotype.Component;

import io.vavr.collection.Array;
import sinnet.grpc.projects.UserToken;

@Component
final class AccessAsOwner implements AccessAs<RoleContextSet.OwnerRoleContext> {

  @Override
  public RoleContextSet.OwnerRoleContext calculate(UserToken userToken) {
    return new RoleContextSet.OwnerRoleContext(Array.empty());
  }
}
