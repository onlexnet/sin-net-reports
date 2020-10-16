package sinnet;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLResolver;

/** Fixme. */
@Component
public class ServicesResolverGet implements GraphQLResolver<Services> {

    @Autowired
    private ActionService actionService;

    public CompletableFuture<ServiceModel> get(Services ignored,
                                               UUID actionId) {
        return actionService
            .find(actionId)
            .map(it -> ServiceModel.builder()
                .entityId(it.getEntityId())
                .entityVersion(it.getVersion())
                .servicemanName(it.getValue().getWho().getValue())
                .whenProvided(it.getValue().getWhen())
                .forWhatCustomer(it.getValue().getWhom().getValue())
                .description(it.getValue().getWhat())
                .duration(it.getValue().getHowLong().getValue())
                .distance(it.getValue().getHowFar().getValue())
                .build())
            .toFuture();
    }

}
