package sinnet.gql.api;

import javax.annotation.Untainted;

import sinnet.grpc.common.UserToken;

/** TBD.  */
public record ActionsMutation(String projectId, UserToken userToken) {
}
