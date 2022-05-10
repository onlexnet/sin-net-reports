package sinnet;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

@Entity
@Table(name = "projects")
@Data
@Accessors(chain = true)
@FieldNameConstants
class ProjectsDbo {

  @Id
  @Column(name = "ENTITY_ID")
  private UUID entityId;

  @Column(name = "EMAIL_OF_OWNER")
  private String emailOfOwner;

  @Column(name = "NAME")
  private String name;
  
}
