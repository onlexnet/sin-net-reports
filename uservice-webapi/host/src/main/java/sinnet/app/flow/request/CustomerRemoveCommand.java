package sinnet.app.flow.request;

import sinnet.domain.models.EntityId;
import sinnet.domain.models.UserToken;

public record CustomerRemoveCommand(EntityId customerId, UserToken requestor) {
}
