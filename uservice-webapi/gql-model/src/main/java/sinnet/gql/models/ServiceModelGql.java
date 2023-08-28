package sinnet.gql.models;

import java.time.LocalDate;

import lombok.Data;
import lombok.experimental.Accessors;

/** GQL model. */
@Data
@Accessors(chain = true)
public class ServiceModelGql {
  private CustomerEntityGql customer;
  private String description;
  private Integer distance;
  private Integer duration;
  private String entityId;
  private long entityVersion;
  private String projectId;
  private String servicemanEmail;
  private String servicemanName;
  private LocalDate whenProvided;
}
