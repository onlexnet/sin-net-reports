package sinnet.action;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;

import io.vavr.collection.List;
import sinnet.ActionService;
import sinnet.Entity;
import sinnet.Name;
import sinnet.ServiceEntity;

@Service
public class ActionServiceImpl implements ActionService {

    @Autowired
    private DatabaseClient client;

    @Override
    public void save(UUID entityId, ServiceEntity entity) {

        var entry = new ActionsDbModel();
        entry.setEntityId(entityId);
        entry.setDescription(entity.getWhat());
        entry.setServicemanName(entity.getWho().getValue());
        entry.setCustomerName(entity.getWhom().getValue());
        entry.setDate(entity.getWhen());
        entry.setDistance(entity.getHowFar().getValue());
        entry.setDuration(entity.getHowLong());

        client
            .insert()
            .into(ActionsDbModel.class)
            .using(entry)
            .then()
            .subscribe();
    }

    @Override
    public List<Entity<ServiceEntity>> find(LocalDate from, LocalDate to) {

        var items = client
            .execute("SELECT entity_id, date, customer_name, description, serviceman_name "
                     + "FROM actions it "
                     + "WHERE it.date >= :from AND it.date <= :to")
            .bind("from", from)
            .bind("to", to)
            .map(row -> ServiceEntity
                .builder()
                .when(row.get("date", LocalDate.class))
                .whom(Name.of(row.get("customer_name", String.class)))
                .what(row.get("description", String.class))
                .who(Name.of(row.get("serviceman_name", String.class)))
                .build()
                .withId(row.get("entity_id", UUID.class)))
            .all()
            .toIterable();
        return List.ofAll(items);
    }

}
