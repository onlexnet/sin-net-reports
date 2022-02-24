package sinnet.gql.security;

import io.smallrye.mutiny.Uni;
import lombok.Value;
import sinnet.grpc.common.UserToken;
import sinnet.grpc.roles.GetReply.Role;

public interface AccessProvider {

  Uni<WithResult> with(String projectId);
  
  @Value
  class WithResult {
      private UserToken userToken;
      private Role projectRole;
  }
}
  