package sinnet;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;
import sinnet.grpc.projects.ListReply;
import sinnet.grpc.projects.ListRequest;
import sinnet.grpc.projects.Project;
import sinnet.grpc.projects.ProjectId;
import sinnet.grpc.projects.ProjectModel;

public interface ProjectList {
  Uni<ListReply> list(ListRequest request);
}

@ApplicationScoped
class ProjectsListImpl implements ProjectList {

  @Inject
  ProjectRepository projectsRepo;

  @Override
  @ReactiveTransactional
  public Uni<ListReply> list(ListRequest request) {
    var emailOfRequestor = request.getEmailOfRequestor();
    return projectsRepo.find("emailOfOwner", emailOfRequestor)
      .stream()
      .map(this::map)
      .collect()
      .asList()
      .map(it -> ListReply.newBuilder().addAllProjects(it).build());
  }

  private Project map(ProjectDbo dbo) {
    return Project.newBuilder()
      .setId(ProjectId.newBuilder().setEId(dbo.getEntityId().toString()).setETag(dbo.getVersion()))
      .setModel(ProjectModel.newBuilder().setName(dbo.getName()))
      .build();
  }

}
