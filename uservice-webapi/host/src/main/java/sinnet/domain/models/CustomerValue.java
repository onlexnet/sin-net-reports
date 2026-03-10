package sinnet.domain.models;

import java.util.List;

import sinnet.gql.models.CustomerContactInputGql;
import sinnet.gql.models.CustomerSecretExInput;
import sinnet.gql.models.CustomerSecretInput;

public record CustomerValue(CustomerEntry entry,
    List<CustomerSecretInput> secrets,
    List<CustomerSecretExInput> secretsEx,
    List<CustomerContactInputGql> contacts) { }
