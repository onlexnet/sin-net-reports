package sinnet;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLResolver;

/** Fixme. */
@Component
public class ServicesOperationsAddNew implements GraphQLResolver<ServicesOperations> {

    @Autowired
    private ActionService actionService;

    /**
     * FixMe.
     *
     * @param ignored ignored
     * @param entry fixme.
     * @return fixme
     */
    public Boolean addNew(final ServicesOperations ignored,
                          final ServiceEntry entry) {

        var model = new ServiceEntity(
            Name.of(entry.getServicemanName()),
            entry.getWhenProvided(),
            Name.of(entry.getForWhatCustomer()),
            entry.getDescription(),
            Duration.ofMinutes(entry.getDuration()),
            Distance.of(entry.getDistance())
        );

        actionService.save(model);

        return true;
    }

}
