package sinnet.infra.adapters.gql;

import java.util.UUID;

import sinnet.domain.models.UserToken;

public record ActionsMutation(UUID projectId, UserToken userToken) {
}
