package sinnet.read;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * TBD.
 */
@Entity
@Table(name = "serviceman")
@Data
@Accessors(chain = true)
public class ServicemanDbo {
  @Id
  @Column(name = "entity_id", columnDefinition = "uniqueidentifier")
  private UUID entityId;
  @Column(name = "email")
  private String email;
  @Column(name = "project_entity_id", columnDefinition = "uniqueidentifier")
  private UUID projectId;
  @Column(name = "entity_version")
  private Integer entityVersion;
  @Column(name = "custom_name")
  private String customName;
}
