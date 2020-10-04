package sinnet;

import java.time.LocalDate;
import java.util.UUID;

import io.vavr.collection.List;
import reactor.core.publisher.Mono;

public interface ActionService {

    Mono<Void> save(UUID entityId, ServiceEntity entity);

    List<Entity<ServiceEntity>> find(LocalDate from, LocalDate to);
}
