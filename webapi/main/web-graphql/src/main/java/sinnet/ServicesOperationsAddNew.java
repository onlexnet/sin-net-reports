package sinnet;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
    // https://www.appsdeveloperblog.com/spring-security-preauthorize-annotation-example/
    // @PreAuthorize("hasAuthority('SCOPE_Actions.Write')")
    public CompletableFuture<Boolean> addNew(final ServicesOperations ignored,
                          final ServiceEntry entry) {

        var model = new ServiceEntity(
            Name.of(entry.getServicemanName()),
            entry.getWhenProvided(),
            Name.of(entry.getForWhatCustomer()),
            entry.getDescription(),
            Duration.ofMinutes(entry.getDuration()),
            Distance.of(entry.getDistance())
        );

        return actionService
            .save(UUID.randomUUID(), model)
            .map(it -> Boolean.TRUE)
            .toFuture();
    }

}
