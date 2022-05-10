package sinnet.gql.models;

import org.eclipse.microprofile.graphql.Ignore;

import lombok.Value;
import sinnet.grpc.common.UserToken;

@Value
public class ProjectsQuery {
  private @Ignore UserToken userToken;
}
