package sinnet.actions;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLResolver;
import sinnet.ActionRepository;
import sinnet.models.EntityId;

/** Fixme. */
@Component
public class ActionsMutationRemove implements GraphQLResolver<ActionsMutation> {

    @Autowired
    private ActionRepository actionService;

    /**
     * FixMe.
     *
     * @param gcontext ignored
     * @param content  fixme.
     * @return fixme
     */
    public CompletionStage<Boolean> remove(ActionsMutation gcontext, UUID entityId, int entityVersion) {
        var projectId = gcontext.getProjectId();
        var id = EntityId.of(projectId, entityId, entityVersion);
        return actionService
            .remove(id)
            .toCompletionStage();
    }

}
