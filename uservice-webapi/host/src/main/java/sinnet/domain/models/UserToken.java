package sinnet.domain.models;

import java.util.UUID;

public record UserToken(UUID projectId, String requestorEmail) {    
}
