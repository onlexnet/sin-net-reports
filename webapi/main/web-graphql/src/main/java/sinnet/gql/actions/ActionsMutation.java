package sinnet.gql.actions;

import java.util.UUID;

import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.Value;

@Value
public class ActionsMutation {
    private UUID projectId;
}


@Component
class ActionsMutationResolver implements GraphQLMutationResolver {

    public ActionsMutation getActions(UUID projectId) {
        return new ActionsMutation(projectId);
    }
}

