package sinnet.rpc;

import io.smallrye.mutiny.Uni;
import sinnet.grpc.projects.CreateReply;
import sinnet.grpc.projects.CreateRequest;

public interface ProjectsCreate {
  Uni<CreateReply> create(CreateRequest request);
}
