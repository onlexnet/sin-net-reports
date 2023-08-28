package sinnet.domain;

import java.util.UUID;

/** TBD. */
public record EntityId(UUID projectId, UUID id, long tag) {
}
