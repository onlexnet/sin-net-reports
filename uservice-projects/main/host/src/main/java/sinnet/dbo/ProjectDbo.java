package sinnet.dbo;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Size;

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
  @Size(max = 50)
  private String emailOfOwner;

  @Column(name = "NAME")
  private String name;
}

