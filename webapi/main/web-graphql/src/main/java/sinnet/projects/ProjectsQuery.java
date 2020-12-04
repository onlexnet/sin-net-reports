package sinnet.projects;

import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLQueryResolver;

@Component
public class ProjectsQuery implements GraphQLQueryResolver {

    public ProjectContext[] getAvailableProjects() {
        return new ProjectContext[0];
    }
}

