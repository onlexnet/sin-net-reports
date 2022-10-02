package sinnet.dbo;

import java.util.Set;
import java.util.UUID;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Table(name = "PROJECT")
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

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "PROJECT_OPERATOR", joinColumns = @JoinColumn(name = "project_id"))
  @Column(name = "email")
  private Set<String> operators;
}


