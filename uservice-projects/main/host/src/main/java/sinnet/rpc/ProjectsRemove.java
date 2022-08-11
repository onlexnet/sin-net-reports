package sinnet.rpc;

import io.smallrye.mutiny.Uni;
import sinnet.grpc.projects.RemoveCommand;
import sinnet.grpc.projects.RemoveResult;

interface ProjectsRemove {
  Uni<RemoveResult> remove(RemoveCommand request);
}
