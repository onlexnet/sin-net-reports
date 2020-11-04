package sinnet;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLResolver;
import lombok.Value;
import sinnet.models.ActionDuration;
import sinnet.models.Distance;
import sinnet.models.Email;
import sinnet.models.Name;

/** Fixme. */
@Component
public class ServicesOperationsNewAction implements GraphQLResolver<ServicesOperations> {

    @Autowired
    private ActionService actionService;

    /**
     * FixMe.
     *
     * @param ignored ignored
     * @param when fixme
     * @return fixme
     */
    public CompletableFuture<Entity> newAction(ServicesOperations ignored,
                                               LocalDate when) {

        var model = new ServiceValue(
            Email.of("undefined@user"),
            when,
            Name.of("Jakiś klient"),
            "Jakaś usługa",
            ActionDuration.empty(),
            Distance.empty()
        );

        var entityId = UUID.randomUUID();
        return actionService
            .save(entityId, model)
            .map(it -> (Entity) new MyEntity(entityId, 1))
            .toFuture();
    }
}

@Value
class MyEntity implements Entity {
    private UUID entityId;
    private int entityVersion;
}
