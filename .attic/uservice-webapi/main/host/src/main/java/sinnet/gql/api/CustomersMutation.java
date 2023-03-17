package sinnet.gql.api;

import javax.annotation.Untainted;

import org.eclipse.microprofile.graphql.Ignore;

import lombok.Value;
import sinnet.grpc.common.UserToken;

@Value
public class CustomersMutation {
  private String projectId;
  @Ignore
  @Untainted 
  private UserToken userToken;
}