package sinnet.domain.models;

import java.util.UUID;

/** Equivalent of [EntityId], already representing a project. */
public record ProjectId(UUID id, long tag) {
}
