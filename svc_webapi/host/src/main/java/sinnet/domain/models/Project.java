package sinnet.domain.models;

import java.util.UUID;

public record Project(
    UUID projectId,
    long tag,
    String name) {    
}
