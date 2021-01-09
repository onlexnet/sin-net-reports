package sinnet.actions;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLResolver;
import io.vertx.core.Future;
import sinnet.ActionRepository;
import sinnet.models.ActionDuration;
import sinnet.models.ActionValue;
import sinnet.models.Distance;
import sinnet.models.Email;
import sinnet.models.EntityId;
import sinnet.models.Name;
import sinnet.read.CustomerProjection;

/** Fixme. */
@Component
public class ActionsMutationUpdate implements GraphQLResolver<ActionsMutation> {

    @Autowired
    private ActionRepository actionService;

    @Autowired
    private CustomerProjection customerReader;

    /**
     * FixMe.
     *
     * @param gcontext ignored
     * @param content fixme.
     * @return fixme
     */
    // https://www.appsdeveloperblog.com/spring-security-preauthorize-annotation-example/
    // @PreAuthorize("hasAuthority('SCOPE_Actions.Write')")
    public CompletionStage<Boolean> update(ActionsMutation gcontext,
                                           UUID entityId,
                                           int entityVersion,
                                           ServiceEntry content) {
        var projectId = gcontext.getProjectId();
        var customerName = Name.of(content.getForWhatCustomer());
        return customerReader
            .get(projectId, customerName)
            .flatMap(it -> it
                .map(v -> {
                    var model = ActionValue.builder()
                        .who(Email.of(content.getServicemanName()))
                        .when(content.getWhenProvided())
                        .whom(v.getId().getId())
                        .what(content.getDescription())
                        .howLong(ActionDuration.of(content.getDuration()))
                        .howFar(Distance.of(content.getDistance()))
                        .build();
                    var id = EntityId.of(projectId, entityId, entityVersion);
                    return actionService
                        .update(id, model)
                        .map(o -> Boolean.TRUE);
                })
                .getOrElse(Future.failedFuture("No data")))
            .toCompletionStage();
    }

}
