package sinnet;

import java.time.LocalDate;

import io.vavr.collection.List;

public interface ActionService {

    void save(ServiceEntity entity);

    List<Entity<ServiceEntity>> find(LocalDate from, LocalDate to);
}
