package sinnet.gql.actions;

import java.time.LocalDate;
import java.util.concurrent.CompletionStage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.GraphQLException;
import graphql.kickstart.tools.GraphQLResolver;
import sinnet.ActionRepository;
import sinnet.IdentityProvider;
import sinnet.gql.SomeEntity;
import sinnet.models.ActionValue;
import sinnet.models.Email;

@Component
public class ActionsMutationNewAction implements GraphQLResolver<ActionsMutation> {

    @Autowired
    private ActionRepository actionService;

    @Autowired
    private IdentityProvider identityProvider;

    public CompletionStage<SomeEntity> newAction(ActionsMutation gcontext, LocalDate whenProvided) {
        var maybeRequestor = identityProvider.getCurrent();
        if (!maybeRequestor.isPresent()) {
          throw new GraphQLException("Access denied");
        }

        var requestor = maybeRequestor.get();
        var emailOfCurrentUser = requestor.getEmail();
        var model = ActionValue.builder()
            .who(Email.of(emailOfCurrentUser))
            .when(whenProvided)
            .build();

        var entityId = sinnet.models.EntityId.anyNew(gcontext.getProjectId());
        return actionService.save(entityId, model)
            .map(it -> new SomeEntity(entityId.getProjectId(), entityId.getId(), 1))
            .toCompletionStage();
    }
}

