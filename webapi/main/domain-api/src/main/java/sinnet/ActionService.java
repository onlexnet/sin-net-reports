package sinnet;

import io.vavr.collection.List;

public interface ActionService {
    void save(ServiceEntity entity);
    List<Entity<ServiceEntity>> find();
}
