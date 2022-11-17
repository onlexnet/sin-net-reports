package sinnet.domain.serviceman;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Entity
@Table(name = "serviceman")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Accessors(chain = true)
public class ServicemanDbo {

  @Column(name = "project_entity_id")
  private UUID projectEntityId;

  @Id
  @EqualsAndHashCode.Include
  @Column(name = "entity_id")
  private UUID entityId;

  @EqualsAndHashCode.Include
  @Column(name = "entity_version")
  private Long entityVersion;

  @Column(name = "custom_name")
  private String customName;

  @Column(name = "email")
  private String email;
}
