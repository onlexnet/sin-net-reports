package sinnet.dbo;

import io.smallrye.mutiny.Uni;
import sinnet.model.ProjectIdHolder;

public interface DboRemove {

    /**
     * Unconditionally removed project with given ID.
     * <br/>
     * Before invocation, be sure that invoker is permitted to remove given project.
     */
    Uni<Void> remove(ProjectIdHolder eid);

}
