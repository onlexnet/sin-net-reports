package sinnet.read;

import java.util.UUID;

import io.vavr.collection.Array;
import io.vertx.core.Future;
import lombok.Value;

public interface Project {

    interface Repository {
        Future<Boolean> save(UUID projectId);
        Future<Array<Entity>> get(String filterByEmail);
    }

    @Value
    class Entity {
        private UUID id;
        private String name;
    }
}
