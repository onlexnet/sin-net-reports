package sinnet.domain.models;

import java.util.List;

public record CustomerValue(CustomerEntry entry,
    List<CustomerSecret> secrets,
    List<CustomerSecretEx> secretsEx,
    List<CustomerContact> contacts) { }
