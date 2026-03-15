package sinnet.app.flow.request;

import java.util.List;

import sinnet.domain.models.CustomerContact;
import sinnet.domain.models.CustomerEntry;
import sinnet.domain.models.CustomerSecret;
import sinnet.domain.models.CustomerSecretEx;
import sinnet.domain.models.EntityId;

public record CustomerGetResult(
	EntityId id,
	CustomerEntry entry,
	List<CustomerSecret> secrets,
	List<CustomerSecretEx> secretsEx,
	List<CustomerContact> contacts) {
}
