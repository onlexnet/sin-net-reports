package sinnet.dbo;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;

import io.vavr.Function1;
import io.vavr.collection.Iterator;
import io.vavr.collection.Seq;
import lombok.RequiredArgsConstructor;
import sinnet.domain.model.ValEmail;
import sinnet.domain.model.ValProjectId;
import sinnet.grpc.projects.generated.Project;
import sinnet.grpc.projects.generated.ProjectId;
import sinnet.grpc.projects.generated.ProjectModel;
import sinnet.read.ServicemanDbo;
import sinnet.read.ServicemanRepo;

@Component
@RequiredArgsConstructor
final class DboGetImpl implements DboGet {

  private final ProjectRepository projectRepository;
  private final ServicemanRepo servicemanRepo;

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
  public Seq<Project> ownedAsProject(ValEmail ownerEmail) {
    return ownedAndMap(ownerEmail, this::mapToEntity);
  }

  @Override
  public Seq<ValProjectId> ownedAsId(ValEmail ownerEmail) {
    return ownedAndMap(ownerEmail, this::mapToIdHolder);
  }

  @Override
  public Project get(ValProjectId projectId) {
    var id = projectId.value();
    return projectRepository
      .findById(id)
      .map(this::mapToEntity).orElseThrow();
  }

  private <T> Seq<T> ownedAndMap(ValEmail emailOfOwner, Function1<ProjectDbo, T> mapper) {
    return projectRepository.findByEmailOfOwner(emailOfOwner.value()).map(mapper).toList();
  }

  @Override
  public StatsResult getStats(ValEmail ownerEmail) {
    // method returns long as it is natural for SQL
    // but we need to return int as it is natural for expected real number of projects.
    var numberOfProjects = (int) projectRepository.countByEmailOfOwner(ownerEmail.value());
    return new StatsResult(numberOfProjects);
  }

  @Override
  public Seq<ValProjectId> assignedAsId(ValEmail operatorEmail) {
    var probe = new ServicemanDbo();
    probe.setEmail(operatorEmail.value());
    var example = Example.of(probe);
    var items = servicemanRepo.findAll(example);
    return Iterator.ofAll(items).map(it -> it.getEntityId()).map(it -> ValProjectId.of(it)).toList();
  }

}
