package sinnet;

import java.time.LocalDate;
import java.util.UUID;

import io.vavr.collection.List;

public interface ActionService {

    void save(UUID entityId, ServiceEntity entity);

    List<Entity<ServiceEntity>> find(LocalDate from, LocalDate to);
}
