package sinnet.access;

import java.util.function.Function;
import java.util.function.Predicate;

import io.smallrye.mutiny.Uni;
import sinnet.grpc.projects.UserToken;
import sinnet.model.ProjectIdHolder;

public interface AccessFacade {
 
  Uni<ProjectIdHolder> guardAccess(UserToken requestor, ProjectIdHolder eid, Function<RoleContext, Predicate<ProjectIdHolder>> methodExtractor);
}
