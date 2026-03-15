package sinnet.adapters.gql;

import java.util.UUID;

import sinnet.domain.models.UserToken;

public record CustomersMutation(UUID projectId, UserToken userToken) {
}
