package sinnet.dbo;

import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;

import io.vavr.Function1;
import io.vavr.collection.Iterator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sinnet.domain.model.ValEmail;
import sinnet.domain.model.ValProjectId;
import sinnet.grpc.projects.generated.Project;
import sinnet.grpc.projects.generated.ProjectId;
import sinnet.grpc.projects.generated.ProjectModel;
import sinnet.read.ServicemanDbo;
import sinnet.read.ServicemanRepo;

@Component
@RequiredArgsConstructor
@Slf4j
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
  public List<Project> ownedAsProject(ValEmail ownerEmail) {
    return ownedAndMap(ownerEmail, this::mapToEntity);
  }

  @Override
  public List<ValProjectId> ownedAsId(ValEmail ownerEmail) {
    return ownedAndMap(ownerEmail, this::mapToIdHolder);
  }

  @Override
  public Project get(ValProjectId projectId) {
    var id = projectId.value();
    return projectRepository
      .findById(id)
      .map(this::mapToEntity).orElseThrow();
  }

  private <T> List<T> ownedAndMap(ValEmail emailOfOwner, Function1<ProjectDbo, T> mapper) {
    return projectRepository.findByEmailOfOwner(emailOfOwner.value()).stream().map(mapper).toList();
  }

  @Override
  public StatsResult getStats(ValEmail ownerEmail) {
    // method returns long as it is natural for SQL
    // but we need to return int as it is natural for expected real number of projects.
    var numberOfProjects = (int) projectRepository.countByEmailOfOwner(ownerEmail.value());
    return new StatsResult(numberOfProjects);
  }

  @Override
  public List<ValProjectId> assignedAsId(ValEmail operatorEmail) {
    var probe = new ServicemanDbo();
    probe.setEmail(operatorEmail.value());
    var example = Example.of(probe);
    var items = servicemanRepo.findAll(example);
    log.info("Assigned projects: {}", items.size());
    return Iterator.ofAll(items).map(it -> it.getProjectId()).map(it -> ValProjectId.of(it)).toJavaList();
  }

  @Override
  public List<Project> getAll(Iterator<ValProjectId> projects) {
    var ids = projects.map(it -> it.value()).toJavaList();
    return Iterator.ofAll(projectRepository.findAllById(ids)).map(this::mapToEntity).toJavaList();
  }

  @Override
  public boolean isOwner(ValEmail email, ValProjectId projectId) {
    return projectRepository.isOwner(email.value(), projectId.value());
  }

}
