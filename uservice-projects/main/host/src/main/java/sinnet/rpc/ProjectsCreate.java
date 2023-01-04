package sinnet.rpc;

import sinnet.grpc.projects.CreateReply;
import sinnet.grpc.projects.CreateRequest;

public interface ProjectsCreate {
  CreateReply create(CreateRequest request);
}
