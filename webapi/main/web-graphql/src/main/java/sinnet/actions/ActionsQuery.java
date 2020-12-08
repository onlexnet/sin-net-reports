package sinnet.actions;

import java.util.UUID;

import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.Value;

@Value
public class ActionsQuery {
    private UUID projectId;
}

@Component
class ActionsQueryResolver implements GraphQLQueryResolver {

    public ActionsQuery getActions(UUID projectId) {
        return new ActionsQuery(projectId);
    }
}

