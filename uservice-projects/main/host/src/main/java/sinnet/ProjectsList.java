package sinnet;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.smallrye.mutiny.Uni;
import sinnet.grpc.projects.AvailableProject;
import sinnet.grpc.projects.ListReply;
import sinnet.grpc.projects.ListRequest;

public interface ProjectsList {
  Uni<ListReply> list(ListRequest request);
}

@ApplicationScoped
class ProjectsListImpl implements ProjectsList {

  @Inject
  ProjectsRepository projectsRepo;

  @Override
  public Uni<ListReply> list(ListRequest request) {
    var emailOfRequestor = request.getEmailOfRequestor();
    // return projectsRepo.find(ProjectsDbo.Fields.emailOfOwner, emailOfRequestor)
    return projectsRepo.find("emailOfOwner", emailOfRequestor)
      .stream()
      .map(it -> AvailableProject.newBuilder()
        .setId(it.getEntityId().toString())
        .setName(it.getName())
        .build())
      .collect()
      .asList()
      .map(it -> ListReply.newBuilder().addAllProjects(it).build());
  }

}
