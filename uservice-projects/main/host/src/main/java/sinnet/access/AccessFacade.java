package sinnet.access;

import java.util.function.Function;
import java.util.function.Predicate;

import io.smallrye.mutiny.Uni;
import sinnet.grpc.projects.UserToken;
import sinnet.model.ValProjectId;

public interface AccessFacade {
 
  Uni<ValProjectId> guardAccess(UserToken requestor, ValProjectId eid, Function<RoleContext, Predicate<ValProjectId>> methodExtractor);
}
