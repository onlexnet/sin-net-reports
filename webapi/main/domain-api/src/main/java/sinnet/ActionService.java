package sinnet;

import java.time.LocalDate;
import java.util.UUID;

import io.vavr.collection.Stream;
import reactor.core.publisher.Mono;
import sinnet.models.Entity;

public interface ActionService {

    Mono<Boolean> save(UUID entityId, ServiceValue entity);

    Mono<Boolean> update(UUID entityId, int entityVersion, ServiceValue entity);

    Mono<Stream<Entity<ServiceValue>>> find(LocalDate from, LocalDate to);

    Mono<Entity<ServiceValue>> find(UUID entityId);
}
