package sinnet.domain.project;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lombok.Data;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectRepository.DboTemplate, UUID> {

  @Entity
  @Table(name = "projects")
  @Data
  static class DboTemplate {
    @Id
    @Column(name = "entity_id")
    private UUID entityId;

    @Version
    @Column(name = "entity_version")
    private int entityVersion;

    @Column(name = "name")
    private String name;
  }
}
