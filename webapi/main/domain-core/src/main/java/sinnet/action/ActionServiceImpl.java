package sinnet.action;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.vavr.collection.List;
import sinnet.ActionService;
import sinnet.Distance;
import sinnet.Entity;
import sinnet.Name;
import sinnet.ServiceEntity;

@Service
@Transactional
public class ActionServiceImpl implements ActionService {

    @Autowired
    private ActionRepository repository;

    @Override
    public void save(ServiceEntity entity) {

        var entry = new ActionDbModel();
        entry.setEntityId(UUID.randomUUID());
        entry.setDescription(entity.getWhat());
        entry.setServicemanName(entity.getWho().getValue());
        repository.save(entry);
    }

    @Override
    public List<Entity<ServiceEntity>> find() {

        return List.ofAll(repository.findAll())
            .map(it -> {
                var value = new ServiceEntity(
                    Name.of("some name"),
                    LocalDate.now(),
                    Name.of("some customer"),
                    it.getDescription(),
                    Duration.ZERO,
                    Distance.of(2)
                );
                return new Entity(it.getEntityId(), value);
            });
    }

}
