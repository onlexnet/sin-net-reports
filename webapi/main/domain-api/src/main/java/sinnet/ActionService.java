package sinnet;

import java.time.LocalDate;
import java.util.UUID;

import io.vavr.collection.Stream;
import reactor.core.publisher.Mono;

public interface ActionService {

    Mono<Void> save(UUID entityId, ServiceEntity entity);

    Mono<Stream<Entity<ServiceEntity>>> find(LocalDate from, LocalDate to);
}
