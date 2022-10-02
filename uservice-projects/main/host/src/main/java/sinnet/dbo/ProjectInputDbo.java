package sinnet.dbo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "INBOX_PROJECT")
class ProjectInputDbo {

  @Id
  @Column(name = "AUTO_ROW_ID")
  private Long id;

  @Column(name = "BATCH_ID")
  private Long batchId;

  @Column(name = "NAME")
  private String name;

  @Column(name = "EMAIL_OF_OWNER")
  private String emailOfOwner;
}
