package sinnet;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.smallrye.mutiny.Uni;
import sinnet.grpc.projects.ProjectId;
import sinnet.grpc.projects.UpdateCommand;
import sinnet.grpc.projects.UpdateResult;

public interface ProjectsUpdate {
  Uni<UpdateResult> update(UpdateCommand request);
}

@ApplicationScoped
class ProjectsUpdateImpl implements ProjectsUpdate {

  @Inject
  ProjectsRepository projectsRepo;

  @Override
  public Uni<UpdateResult> update(UpdateCommand request) {
    var idAsString = request.getEntityId().getProjectId();
    var id = UUID.fromString(idAsString);
    var name = request.getName();
    var emailOfOwner = request.getEmailOfOwner();
    var entity = new ProjectsDbo()
        .setEntityId(id)
        .setName(name)
        .setEmailOfOwner(emailOfOwner);
    return projectsRepo.persistAndFlush(entity)
      .map(it -> ProjectId.newBuilder()
        .setProjectId(it.getEntityId().toString())
        .setEntityVersion(1).build())
      .map(it -> UpdateResult.newBuilder().setEntityId(it).build());
  }

}
