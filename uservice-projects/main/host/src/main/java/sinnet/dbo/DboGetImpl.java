package sinnet.dbo;

import javax.enterprise.context.ApplicationScoped;

import org.hibernate.reactive.mutiny.Mutiny;

import io.smallrye.mutiny.Uni;
import io.vavr.Function1;
import io.vavr.collection.Array;
import lombok.RequiredArgsConstructor;
import sinnet.grpc.projects.Project;
import sinnet.grpc.projects.ProjectId;
import sinnet.grpc.projects.ProjectModel;
import sinnet.model.ValEmail;
import sinnet.model.ValProjectId;

@ApplicationScoped
@RequiredArgsConstructor
final class DboGetImpl implements DboGet {

  private final ProjectRepository projectRepository;

  private ValProjectId mapToIdHolder(ProjectDbo dbo) {
    return ValProjectId.of(dbo.getEntityId());
  }

  private ProjectId mapToId(ProjectDbo dbo) {
    return ProjectId.newBuilder()
      .setEId(dbo.getEntityId().toString())
      .setETag(dbo.getVersion())
      .build();
  }

  private ProjectModel mapToModel(ProjectDbo dbo) {
    return ProjectModel.newBuilder()
      .setEmailOfOwner(dbo.getEmailOfOwner())
      .setName(dbo.getName())
      .addAllEmailOfOperator(dbo.getOperators())
      .build();
  }

  private Project mapToEntity(ProjectDbo dbo) {
    return Project.newBuilder()
      .setId(mapToId(dbo))
      .setModel(mapToModel(dbo))
      .build();
  }

  @Override
  public Uni<Array<Project>> ownedAsProject(ValEmail ownerEmail) {
    return ownedAndMap(ownerEmail, this::mapToEntity);
  }

  @Override
  public Uni<Array<ValProjectId>> ownedAsId(ValEmail ownerEmail) {
    return ownedAndMap(ownerEmail, this::mapToIdHolder);
  }

  @Override
  public Uni<Project> get(ValProjectId projectId) {
    var id = projectId.value();
    return projectRepository
      .findById(id)
      .map(this::mapToEntity);
  }

  private <T> Uni<Array<T>> ownedAndMap(ValEmail emailOfOwner, Function1<ProjectDbo, T> mapper) {
    return projectRepository
      .list("select t from ProjectDbo t where t.emailOfOwner = ?1", emailOfOwner.value())
      .map(it -> Array.ofAll(it).map(mapper));
  }

}