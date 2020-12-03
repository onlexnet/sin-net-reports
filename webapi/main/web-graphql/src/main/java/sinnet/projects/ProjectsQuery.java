package sinnet.projects;

import java.util.concurrent.CompletableFuture;

import graphql.kickstart.tools.GraphQLQueryResolver;

public class ProjectsQuery implements GraphQLQueryResolver {

    public CompletableFuture<ProjectToken[]> projects() {
        return null;
    }
}
