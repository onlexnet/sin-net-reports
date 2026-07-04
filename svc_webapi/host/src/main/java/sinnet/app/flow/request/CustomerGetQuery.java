package sinnet.app.flow.request;

import sinnet.domain.models.UserToken;

public record CustomerGetQuery(UserToken userToken, String entityId) {
}
