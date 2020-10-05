package sinnet;

import java.time.LocalDate;
import java.util.UUID;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ActionService {

    Mono<Void> save(UUID entityId, ServiceEntity entity);

    Flux<Entity<ServiceEntity>> find(LocalDate from, LocalDate to);
}
