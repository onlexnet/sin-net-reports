package sinnet.gql.api;

import java.util.UUID;

import sinnet.domain.models.UserToken;

/** Fixme. */
public record CustomersMutation(UUID projectId, UserToken userToken, sinnet.grpc.common.UserToken legacyUserToken) {
}
