package sinnet.app.flow.models;

import java.time.LocalDateTime;
import java.util.List;

import sinnet.gql.models.CustomerContactInputGql;
import sinnet.gql.models.CustomerInput;
import sinnet.gql.models.CustomerSecretExInput;
import sinnet.gql.models.CustomerSecretInput;
import sinnet.gql.models.EntityGql;
import sinnet.grpc.common.UserToken;

public record CustomerUpdateCommand(UserToken userToken,
    EntityGql id, CustomerInput entry, List<CustomerSecretInput> secrets, List<CustomerSecretExInput> secretsEx,
    List<CustomerContactInputGql> contacts, LocalDateTime changedWhen, String changedWho) { }
