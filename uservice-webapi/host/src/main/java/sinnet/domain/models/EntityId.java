package sinnet.domain.models;

import java.util.UUID;

/**
 * All entities are partitioned by projects, with its own ID and version
 * It forces to use partitioning and verion-safe operations.
 */
public record EntityId(UUID projectId, UUID id, long tag) {
}
