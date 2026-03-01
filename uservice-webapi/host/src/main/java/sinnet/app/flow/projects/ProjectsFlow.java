package sinnet.app.flow.projects;

import java.util.List;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sinnet.app.ports.in.ProjectsPortIn;
import sinnet.app.ports.out.ProjectsPortOut;
import sinnet.domain.models.ProjectId;
import sinnet.gql.models.ProjectEntityGql;
import sinnet.grpc.projects.generated.Project;

@Component
@RequiredArgsConstructor
class ProjectsFlow implements ProjectsPortIn {

    private final ProjectsPortOut projectsOutPort;

    @Override
    public ProjectId create(String requestorEmail) {
        return projectsOutPort.create(requestorEmail);
    }

    @Override
    public ProjectId update(String requestorEmail, ProjectId id, String name, String ownerEmail, List<String> operatorEmail) {
        return projectsOutPort.update(requestorEmail, id, name, ownerEmail, operatorEmail);
    }

    @Override
    public List<ProjectEntityGql> list(String requestorEmail, Function<Project, ProjectEntityGql> mapper) {
        return projectsOutPort.list(requestorEmail, mapper);
    }

    @Override
    public StatsResult userStats(String requestorEmail) {
        var result = projectsOutPort.userStats(requestorEmail);
        return new StatsResult(result.numberOfProjects());
    }
    
}
