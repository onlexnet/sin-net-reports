package sinnet.rpc;

import io.smallrye.mutiny.Uni;
import sinnet.grpc.projects.GetReply;
import sinnet.grpc.projects.GetRequest;

public interface ProjectGet {
  
  Uni<GetReply> get(GetRequest request);
}