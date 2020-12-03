package sinnet;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLResolver;

/** Fixme. */
@Component
public class ServicesOperationsResolverGet implements GraphQLResolver<ServicesOperations> {

    @Autowired
    private ActionRepository actionService;

    public CompletionStage<ServiceModel> get(ServicesOperations gcontext,
                                             UUID actionId) {
        var projectId = gcontext.getProjectId();
        return actionService
            .find(projectId, actionId)
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
            .toCompletionStage();
    }

}
