package sinnet.app.flow.projects;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sinnet.app.ports.in.ProjectsPortIn;
import sinnet.app.ports.out.ProjectsPortOut;
import sinnet.domain.models.Project;
import sinnet.domain.models.ProjectId;

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
    public List<Project> list(String requestorEmail) {
        return projectsOutPort.list(requestorEmail);
    }

    @Override
    public StatsResult userStats(String requestorEmail) {
        var result = projectsOutPort.userStats(requestorEmail);
        return new StatsResult(result.numberOfProjects());
    }
    
}
