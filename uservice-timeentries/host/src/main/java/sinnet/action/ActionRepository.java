package sinnet.action;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * TBD.
 */
@Repository
public interface ActionRepository extends JpaRepository<ActionRepository.ActionDbo, UUID> {
  
  void deleteByProjectIdAndEntityIdAndEntityVersion(UUID projectId, UUID entityId, long version);

  ActionDbo findByProjectIdAndEntityIdAndEntityVersion(UUID projectId, UUID entityId, long version);

  ActionDbo findByProjectIdAndEntityId(UUID projectId, UUID entityId);

  List<ActionDbo> findByProjectIdAndDateGreaterThanEqualAndDateLessThanEqual(UUID projectId, LocalDate from, LocalDate to);

  /**
   * TBD.
   */
  @Entity
  @Table(name = "actions")
  @Data
  @Accessors(chain = true)
  final class ActionDbo {

    // @Type(type = "uuid-char")
    @Column(name = "project_id", columnDefinition = "uniqueidentifier")
    private UUID projectId;

    @Id
    @Column(name = "entity_id", columnDefinition = "uniqueidentifier")
    private UUID entityId;

    @Version
    @Column(name = "entity_version")
    private Long entityVersion;

    @Column(name = "serviceman_email")
    private String servicemanEmail;

    @Column(name = "description")
    private String description;

    @Column(name = "distance")
    private Integer distance;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "serviceman_name")
    private String servicemanName;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "customer_id", columnDefinition = "uniqueidentifier")
    private UUID customerId;
  }
}
