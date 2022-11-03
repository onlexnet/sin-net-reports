package sinnet.events;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Component
@RequiredArgsConstructor
class ProjectCreatedEventHandler {

  private final ProjectRepository repository;

  @EventListener
  void on(ProjectCreated event) {
    var newEntity = new ProjectEntity()
        .setEntityId(event.getProjectId())
        .setEntityVersion(event.getProjectVersion())
        .setName(event.getName());
    repository.save(newEntity);
  }

}

@Entity
@Table(name = "projects")
@Data
@Accessors(chain = true)
class ProjectEntity {

  @Id
  @Column(name = "ENTITY_ID")
  private UUID entityId;

  @Version
  @Column(name = "ENTITY_VERSION")
  private Long entityVersion;

  @Column(name = "NAME")
  private String name;
}

@Repository
interface ProjectRepository extends JpaRepository<ProjectEntity, UUID> {
}
