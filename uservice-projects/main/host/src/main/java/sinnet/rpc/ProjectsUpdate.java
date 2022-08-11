package sinnet.rpc;

import io.smallrye.mutiny.Uni;
import sinnet.grpc.projects.UpdateCommand;
import sinnet.grpc.projects.UpdateResult;

public interface ProjectsUpdate {

  Uni<UpdateResult> update(UpdateCommand request);
}
