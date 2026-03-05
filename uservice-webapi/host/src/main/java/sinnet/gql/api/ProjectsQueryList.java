package sinnet.gql.api;

import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import sinnet.app.ports.in.ProjectsPortIn;
import sinnet.gql.models.ProjectEntityGql;
import sinnet.infra.adapters.gql.ProjectsMapper;

@Controller
@RequiredArgsConstructor
class ProjectsQueryList {

  private final ProjectsPortIn service;

  @SchemaMapping
  List<ProjectEntityGql> list(ProjectsQuery self, @Argument String name) {
    var requestorEmail = self.requestorEmail();
    var result = service.list(requestorEmail);
    return result.stream().map(ProjectsMapper::map).toList();
  }

  @SchemaMapping
  Integer numberOfProjects(ProjectsQuery self) {
    var requestorEmail = self.requestorEmail();
    return service.userStats(requestorEmail).numberOfProjects();
  }
}


