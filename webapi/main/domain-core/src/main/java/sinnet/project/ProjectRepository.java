package sinnet.project;

import java.util.UUID;

import io.vertx.core.Future;

public interface ProjectRepository {
    Future<Boolean> save(UUID projectId);
}
