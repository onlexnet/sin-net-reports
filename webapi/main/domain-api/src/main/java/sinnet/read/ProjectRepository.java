package sinnet.read;

import java.util.UUID;

import io.vavr.collection.Array;
import io.vertx.core.Future;

public interface ProjectRepository {
    Future<Boolean> save(UUID projectId);

    Future<Array<UUID>> get(String filterByEmail);
}
