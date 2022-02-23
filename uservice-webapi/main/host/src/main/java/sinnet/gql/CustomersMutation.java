package sinnet.gql;

import org.eclipse.microprofile.graphql.Ignore;

import lombok.Value;
import sinnet.grpc.common.UserToken;

@Value
public class CustomersMutation {
  private String projectId;
  @Ignore
  private UserToken userToken;
}
