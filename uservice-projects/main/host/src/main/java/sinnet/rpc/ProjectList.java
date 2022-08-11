package sinnet.rpc;

import io.smallrye.mutiny.Uni;
import sinnet.grpc.projects.ListReply;
import sinnet.grpc.projects.ListRequest;

public interface ProjectList {
  Uni<ListReply> list(ListRequest request);
}