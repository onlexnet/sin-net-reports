package sinnet;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import io.smallrye.mutiny.Uni;
import sinnet.grpc.projects.ProjectId;
import sinnet.grpc.projects.ReserveReply;
import sinnet.grpc.projects.ReserveRequest;

public interface ProjectsReserve {
  Uni<ReserveReply> reserve(ReserveRequest request);
}

@ApplicationScoped
class ProjectsReserveImpl implements ProjectsReserve {

  public Uni<ReserveReply> reserve(ReserveRequest request) {
    var reply = ReserveReply.newBuilder()
        .setEntityId(ProjectId.newBuilder()
            .setProjectId(UUID.randomUUID().toString())
            .setEntityVersion(0))
        .build();
    return Uni.createFrom().item(reply);
  }

}
