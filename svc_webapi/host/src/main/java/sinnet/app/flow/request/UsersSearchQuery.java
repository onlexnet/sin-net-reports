package sinnet.app.flow.request;

import java.util.UUID;

public record UsersSearchQuery(UUID projectId, String requestorEmail) {
}