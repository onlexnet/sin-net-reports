package sinnet.projects;

import static java.util.concurrent.CompletableFuture.completedStage;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLQueryResolver;
import sinnet.IdentityProvider;
import sinnet.read.ProjectRepository;

@Component
public class ProjectsQuery implements GraphQLQueryResolver {

    @Autowired
    private IdentityProvider identityProvider;

    @Autowired
    private ProjectRepository projectRepository;

    private UUID[] empty = new UUID[0];

    public CompletionStage<UUID[]> getAvailableProjects() {
        var maybeUser = identityProvider.getCurrent();
        if (!maybeUser.isPresent()) return completedStage(empty);

        var user = maybeUser.get();
        return projectRepository
            .get(user.getEmail())
            .map(it -> it.toJavaArray(UUID[]::new))
            .toCompletionStage();
    }
}

