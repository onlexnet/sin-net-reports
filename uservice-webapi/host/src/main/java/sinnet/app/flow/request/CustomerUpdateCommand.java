package sinnet.app.flow.request;

import java.time.LocalDateTime;

import sinnet.domain.models.CustomerValue;
import sinnet.gql.models.EntityGql;
import sinnet.grpc.common.UserToken;

public record CustomerUpdateCommand(
    UserToken userToken,
    EntityGql id,
    CustomerValue value,
    LocalDateTime changedWhen,
    String changedWho) { }
