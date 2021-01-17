package sinnet.projects;

import static java.util.concurrent.CompletableFuture.completedStage;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.Value;
import sinnet.IdentityProvider;
import sinnet.read.Project;

public interface ProjectsQuery {

    @Component
    class Resolver implements GraphQLQueryResolver {

        @Autowired
        private IdentityProvider identityProvider;

        @Autowired
        private Project.Repository projectRepository;

        private ProjectEntity[] empty = new ProjectEntity[0];

        public CompletionStage<ProjectEntity[]> getAvailableProjects() {
            var maybeUser = identityProvider.getCurrent();
            if (!maybeUser.isPresent()) return completedStage(empty);

            var user = maybeUser.get();
            return projectRepository
                .get(user.getEmail())
                .map(it -> it
                    .map(o -> new ProjectEntity(o.getId(), o.getName()))
                    .toJavaArray(ProjectEntity[]::new))
                .toCompletionStage();
        }
    }

    @Value
    class ProjectEntity {
        private UUID id;
        private String name;
    }
}

