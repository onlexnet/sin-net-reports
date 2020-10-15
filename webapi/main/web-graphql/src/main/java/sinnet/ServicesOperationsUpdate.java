package sinnet;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLResolver;
import sinnet.models.ActionDuration;
import sinnet.models.Distance;
import sinnet.models.Email;
import sinnet.models.Name;

/** Fixme. */
@Component
public class ServicesOperationsUpdate implements GraphQLResolver<ServicesOperations> {

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
    public CompletableFuture<Boolean> update(ServicesOperations ignored,
                                             ServiceEntry entry) {

        var model = new ServiceValue(
            Email.of(entry.getServicemanName()),
            entry.getWhenProvided(),
            Name.of(entry.getForWhatCustomer()),
            entry.getDescription(),
            ActionDuration.of(entry.getDuration()),
            Distance.of(entry.getDistance())
        );

        return actionService
            .update(UUID.randomUUID(), model)
            .map(it -> Boolean.TRUE)
            .toFuture();
    }

}
