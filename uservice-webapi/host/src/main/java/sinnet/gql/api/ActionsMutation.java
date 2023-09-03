package sinnet.gql.api;

import sinnet.grpc.common.UserToken;

/** TBD.  */
public record ActionsMutation(String projectId, UserToken userToken) {
}
