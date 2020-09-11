package sinnet.action;

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
        entry.setCustomerName(entity.getWhom().getValue());
        entry.setWhen(entity.getWhen());
        entry.setDistance(entity.getHowFar().getValue());
        entry.setDuration(entity.getHowLong());
        repository.save(entry);
    }

    @Override
    public List<Entity<ServiceEntity>> find(LocalDate from, LocalDate to) {

        return List
            .ofAll(repository.findForPeriod(from, to))
            .map(it -> {
                var value = new ServiceEntity(
                    Name.of(it.getServicemanName()),
                    it.getWhen(),
                    Name.of(it.getCustomerName()),
                    it.getDescription(),
                    it.getDuration(),
                    Distance.of(it.getDistance()));
                return new Entity<>(it.getEntityId(), value);
            });
    }

}
