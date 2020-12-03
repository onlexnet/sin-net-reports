package sinnet;

import java.time.LocalDate;
import java.util.concurrent.CompletionStage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLResolver;
import sinnet.models.ActionValue;
import sinnet.models.Email;
import sinnet.models.Name;

/** Fixme. */
@Component
public class ServicesOperationsNewAction implements GraphQLResolver<ServicesOperations> {

    @Autowired
    private ActionRepository actionService;

    /**
     * FixMe.
     *
     * @param gcontext      ignored
     * @param whenProvided fixme
     * @return fixme
     */
    public CompletionStage<SomeEntity> newAction(ServicesOperations gcontext, LocalDate whenProvided) {

        var model = ActionValue.builder()
            .who(Email.of("undefined@user"))
            .when(whenProvided)
            .whom(Name.of("Jakiś klient"))
            .what("Jakaś usługa")
            .build();

        var entityId = sinnet.models.EntityId.anyNew(gcontext.getProjectId());
        return actionService.save(entityId, model)
            .map(it -> new SomeEntity(entityId.getProjectId(), entityId.getId(), 1))
            .toCompletionStage();
    }
}

