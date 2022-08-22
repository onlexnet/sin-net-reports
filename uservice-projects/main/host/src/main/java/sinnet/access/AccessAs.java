package sinnet.access;

import javax.enterprise.context.ApplicationScoped;

import io.smallrye.mutiny.Uni;
import io.vavr.collection.Array;
import lombok.RequiredArgsConstructor;
import sinnet.dbo.DboFacade;
import sinnet.grpc.projects.UserToken;
import sinnet.model.ValEmail;

/** Calculates for given {@code UserToken} what are  */
interface AccessAs<T extends RoleContextSet> {
  
  Uni<T> calculate(UserToken userToken);
}

@ApplicationScoped
@RequiredArgsConstructor
final class AccessAsOwner implements AccessAs<RoleContextSet.OwnerRoleContext> {

  private final DboFacade dbo;

  @Override
  public Uni<RoleContextSet.OwnerRoleContext> calculate(UserToken userToken) {
    var ownerEmail = ValEmail.of(userToken.getRequestorEmail());
    return dbo.ownedAsId(ownerEmail)
      .map(RoleContextSet.OwnerRoleContext::new);
  }

}

@ApplicationScoped
final class AccessAsOperator implements AccessAs<RoleContextSet.OperatorRoleContext> {

  @Override
  public Uni<RoleContextSet.OperatorRoleContext> calculate(UserToken userToken) {
    var result = new RoleContextSet.OperatorRoleContext(Array.empty());
    return Uni.createFrom().item(result);
  }
}