package sinnet.app.flow.request;

import java.time.LocalDateTime;

import sinnet.domain.models.EntityId;
import sinnet.domain.models.CustomerValue;
import sinnet.domain.models.UserToken;

public record CustomerUpdateCommand(
    UserToken userToken,
    EntityId id,
    CustomerValue value,
    LocalDateTime changedWhen,
    String changedWho) { }
