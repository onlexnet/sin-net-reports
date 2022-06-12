package sinnet;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Table(name = "PROJECTS")
@Data
@Accessors(chain = true)
class ProjectDbo {

  @Id
  @Column(name = "ENTITY_ID")
  private UUID entityId;

  @Version
  @Column(name = "ENTITY_VERSION")
  private Long version;

  @Column(name = "EMAIL_OF_OWNER")
  private String emailOfOwner;

  @Column(name = "NAME")
  private String name;
  
  /** to delete - used right now just to test if it is uncontrolled entity. */
  @Transient
  private boolean temp = false;
}
