package sinnet.app.flow.request;

import java.time.LocalDateTime;

import sinnet.domain.models.CustomerValue;
import sinnet.domain.models.UserToken;
import sinnet.gql.models.EntityGql;

public record CustomerUpdateCommand(
    UserToken userToken,
    EntityGql id,
    CustomerValue value,
    LocalDateTime changedWhen,
    String changedWho) { }
