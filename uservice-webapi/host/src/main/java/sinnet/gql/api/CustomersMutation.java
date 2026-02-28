package sinnet.gql.api;

import sinnet.grpc.common.UserToken;

/** Fixme. */
public record CustomersMutation(String projectId, UserToken userToken) {
}
