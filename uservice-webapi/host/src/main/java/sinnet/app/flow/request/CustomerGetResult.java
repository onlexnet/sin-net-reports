package sinnet.app.flow.request;

import sinnet.domain.models.CustomerValue;
import sinnet.domain.models.EntityId;

public record CustomerGetResult(
	EntityId id,
	CustomerValue value) {
}
