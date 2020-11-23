package sinnet;

import java.time.LocalDate;
import java.util.UUID;

import io.vavr.collection.Stream;
import reactor.core.publisher.Mono;
import sinnet.models.Entity;
import sinnet.models.ActionValue;

public interface ActionService {

    Mono<Boolean> save(UUID entityId, ActionValue entity);

    Mono<Boolean> update(UUID entityId, int entityVersion, ActionValue entity);

    Mono<Stream<Entity<ActionValue>>> find(LocalDate from, LocalDate to);

    Mono<Entity<ActionValue>> find(UUID entityId);
}
