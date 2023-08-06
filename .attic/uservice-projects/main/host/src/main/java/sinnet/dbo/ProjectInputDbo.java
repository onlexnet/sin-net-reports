package sinnet.dbo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
